package com.zeyadgasser.core

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

/**
 * A base viewModel class that implements a UnidirectionalDataFlow (UDF) pattern using [Flow]s.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
abstract class FluxViewModel<I : Input, R : Result, S : State, E : Effect>(
    val initialState: S,
    initialInput: I? = null,
    private val savedStateHandle: SavedStateHandle? = null,
    private val reducer: Reducer<S, R>? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    companion object {
        const val ARG_STATE_KEY = "arg_state_key"
        private const val DELAY = 100L
    }

    private lateinit var job: Job
    private var currentState: S = initialState

    private val cancellableInputsMap: MutableMap<KClass<out I>, AtomicBoolean> = mutableMapOf()
    private val tag: String = this::class.simpleName.orEmpty()
    private val inputs: MutableSharedFlow<Input> = MutableSharedFlow()
    private val throttledInputs: MutableSharedFlow<Input> = MutableSharedFlow()
    private val debouncedInputs: MutableSharedFlow<Input> = MutableSharedFlow()
    private val viewModelListener: MutableStateFlow<Output> = MutableStateFlow(currentState)

    init {
        activate()
        initialInput?.let { process(it) }
    }

    /**
     * Call observe from view to listen to state and effect updates.
     *
     * @return a [StateFlow] of [Output] representing the state and effect updates.
     */
    fun observe(): StateFlow<Output> = viewModelListener.asStateFlow()

    /**
     * Send inputs: I to be processed by ViewModel.
     *
     * @param input the input to be processed.
     */
    fun process(input: Input): Unit = viewModelScope.launch {
        when (input.inputStrategy) {
            NONE -> inputs
            is Throttle -> throttledInputs
            is Debounce -> debouncedInputs
        }.emit(input)
    }.let {}

    /**
     * Returns a new Flow that is cancellable by the [Input].
     * Make sure to cancel while the other action is in progress.
     *
     * @param [inputClass] The input that can be used to cancel the flow.
     * @return A new Flow that is cancellable by [Input] I.
     */
    fun Flow<Result>.makeCancellable(inputClass: KClass<out I>): Flow<Result> =
        onStart { cancellableInputsMap[inputClass] = AtomicBoolean(false) }
            .takeWhile { cancellableInputsMap[inputClass]?.get() == false }
            .onCompletion { cancellableInputsMap.remove(inputClass) }

    /**
     * Map inputs: I with current state: S to a [Flow] of [Result]s.
     *
     * @param input the input to be handled.
     * @param state the current state of the ViewModel.
     * @return a [Flow] of [Result] representing the Results of handling the input.
     */
    abstract fun handleInputs(input: I, state: S): Flow<Result>

    /**
     * Override to implement with your preferred logger.
     */
    protected open fun log(loggable: Loggable): Unit = Log.d(tag, "$loggable").let { }

    /**
     * Activates the UnidirectionalDataFlow (UDF) by creating the stream that connects the inputs streams to
     * the [viewModelListener]
     */
    @Suppress("UNCHECKED_CAST")
    private fun activate(): Unit = createResults().shareIn(viewModelScope, Lazily).let { resultFlow ->
        val states: Flow<Output> = resultFlow.createStates(reducer)
        val nonStates: Flow<Output> =
            resultFlow.filter { it is Progress || it is Error || it is Effect } as Flow<Output>
        job = viewModelScope.launch(dispatcher) { merge(nonStates, states).collect { it.handleOutput() } }
    }

    /**
     * Merges the input [Flow]s, log each emission, then calls [handleInputs] to map the inputs to actions
     * that can be executed in ([flatMapConcat]) or out([flatMapMerge]) of order. Also calls
     * [processInputResultStream] to apply the Loading, Success & Error (LSE) pattern.
     */
    private fun createResults(): Flow<Result> = merge(
        inputs,
        throttledInputs.onEach { delay(it.inputStrategy.interval) },
        debouncedInputs.debounce { it.inputStrategy.interval }
    ).map { input ->
        log(input)
        InputResultFlowPair(input, processInput(input))
    }.flowOn(dispatcher).shareIn(viewModelScope, Lazily).run {
        // create two streams one for sync and one for async processing
        val asyncOutcomes: Flow<Result> = filter { it.resultFlow is AsyncResultFlow }
            .map { it.copy(resultFlow = (it.resultFlow as AsyncResultFlow).flow) }
            .flatMapMerge { processInputResultStream(it) }
        val sequentialOutcomes: Flow<Result> = filter { it.resultFlow !is AsyncResultFlow }
            .flatMapConcat { processInputResultStream(it) }
        merge(asyncOutcomes, sequentialOutcomes).flowOn(dispatcher)// merge them back into a single stream
    }

    @Suppress("UNCHECKED_CAST")
    private fun processInput(input: Input): Flow<Result> = if (input is CancelInput<*>) emptyResultFlow()
        .also { cancellableInputsMap[input.clazz as KClass<I>] = AtomicBoolean(true) }
    else handleInputs(input as I, currentState)

    /**
     * Applies the Loading, Success & Error (LSE) pattern to every [Result]
     */
    private fun processInputResultStream(stream: InputResultFlowPair): Flow<Result> {
        val resultFlow: Flow<Result> = stream.resultFlow
            .catch { cause -> emit(Error(cause.message.orEmpty(), cause, stream.input)) } // wrap uncaught exceptions
        return if (stream.input.getShowProgress()) { // emit Progress true and false around the result
            resultFlow.flatMapConcat { flowOf(it, Progress(false, stream.input)).onEach { delay(DELAY) } }
                .onStart { emit(Progress(true, stream.input)) }
        } else resultFlow
    }

    /**
     * Given a [Flow] of [Result]s return a [Flow] of [State]s by filtering and applying the [reducer]
     * if exists (MVI vs MVVM).
     */
    @Suppress("UNCHECKED_CAST")
    private fun Flow<Result>.createStates(reducer: Reducer<S, R>?): Flow<S> =
        if (reducer == null)  // MVI
            mapNotNull { if (it is State) it as S else null } // MVVM
        else mapNotNull { if (it !is Progress && it !is Error && it !is EmptyResult && it !is Effect) it as R else null } // MVI
            .scan(currentState) { state: S, result: R -> reducer.reduce(state, result).also { log(result) } }

    /**
     * Passes and logs [Result]s to [viewModelListener] to be observed by the view
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun Output.handleOutput(): Unit = when (this) {
        is Error -> viewModelListener.emit(this)
        is Progress -> viewModelListener.emit(this)
        is Effect -> viewModelListener.emit(this)
        is State -> (this as S).takeIf { it != currentState }?.let { state ->
            savedStateHandle?.set(ARG_STATE_KEY, state)
            currentState = state
            viewModelListener.emit(state)
        } ?: Unit
    }.also { log(this) }

    /**
     * Cancels the UDF when the ViewModel is destroyed.
     */
    final override fun onCleared(): Unit = job.cancel()
}
