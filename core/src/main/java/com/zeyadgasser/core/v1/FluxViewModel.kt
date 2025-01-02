package com.zeyadgasser.core.v1

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadgasser.core.v1.Outcome.EmptyOutcome
import com.zeyadgasser.core.v1.Outcome.ErrorOutcome
import com.zeyadgasser.core.v1.Outcome.ProgressOutcome
import com.zeyadgasser.core.v1.api.AsyncOutcomeFlow
import com.zeyadgasser.core.v1.api.CancelInput
import com.zeyadgasser.core.v1.api.Debounce
import com.zeyadgasser.core.v1.api.Effect
import com.zeyadgasser.core.v1.api.Input
import com.zeyadgasser.core.v1.api.NONE
import com.zeyadgasser.core.v1.api.Output
import com.zeyadgasser.core.v1.api.Reducer
import com.zeyadgasser.core.v1.api.Result
import com.zeyadgasser.core.v1.api.State
import com.zeyadgasser.core.v1.api.Throttle
import com.zeyadgasser.core.v1.api.emptyOutcomeFlow
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

    data class EffectOutcome<E>(val effect: E, override val showProgress: Boolean) : Outcome(showProgress)
    data class ResultOutcome<R>(val result: R, override val showProgress: Boolean) : Outcome(showProgress)
    data class StateOutcome<S>(val state: S, override val showProgress: Boolean) : Outcome(showProgress)

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
     * Returns a new Flow that is cancellable by the [input].
     * Make sure to cancel while the other action is in progress.
     *
     * @param [inputClass] The input that can be used to cancel the flow.
     * @return A new Flow that is cancellable by [input] I.
     */
    fun Flow<Outcome>.makeCancellable(inputClass: KClass<out I>): Flow<Outcome> =
        onStart { cancellableInputsMap[inputClass] = AtomicBoolean(false) }
            .takeWhile { cancellableInputsMap[inputClass]?.get() == false }
            .onCompletion { cancellableInputsMap.remove(inputClass) }

    /**
     * Map inputs: I with current state: S to a [Flow] of [Outcome]s.
     *
     * @param input the input to be handled.
     * @param state the current state of the ViewModel.
     * @return a [Flow] of [Outcome] representing the outcomes of handling the input.
     */
    abstract fun handleInputs(input: I, state: S): Flow<Outcome>

    /**
     * Override to implement with your preferred logger.
     */
    protected open fun log(loggable: Loggable): Unit = Log.d(tag, "$loggable").let { }

    /**
     * Activates the UnidirectionalDataFlow (UDF) by creating the stream that connects the inputs streams to
     * the [viewModelListener]
     */
    private fun activate(): Unit = createOutcomes().shareIn(viewModelScope, Lazily).let { outcomeFlow ->
        val states = outcomeFlow.createStates()
        val nonStates = outcomeFlow.filter { it !is StateOutcome<*> && it !is ResultOutcome<*> }
        job = viewModelScope.launch(dispatcher) { merge(nonStates, states).collect { it.handleOutcome() } }
    }

    /**
     * Merges the input [Flow]s, log each emission, then calls [handleInputs] to map the inputs to actions
     * that can be executed in ([flatMapConcat]) or out([flatMapMerge]) of order. Also calls
     * [processInputOutcomeStream] to apply the Loading, Success & Error (LSE) pattern.
     */
    private fun createOutcomes(): Flow<Outcome> = merge(
        inputs,
        throttledInputs.onEach { delay(it.inputStrategy.interval) },
        debouncedInputs.debounce { it.inputStrategy.interval }
    ).map { input ->
        log(input)
        InputOutcomeStream(input, processInput(input))
    }.flowOn(dispatcher).shareIn(viewModelScope, Lazily).run {
        // create two streams one for sync and one for async processing
        val asyncOutcomes = filter { it.outcomes is AsyncOutcomeFlow }
            .map { it.copy(outcomes = (it.outcomes as AsyncOutcomeFlow).flow) }
            .flatMapMerge { processInputOutcomeStream(it) }
        val sequentialOutcomes = filter { it.outcomes !is AsyncOutcomeFlow }
            .flatMapConcat { processInputOutcomeStream(it) }
        merge(asyncOutcomes, sequentialOutcomes).flowOn(dispatcher)// merge them back into a single stream
    }

    @Suppress("UNCHECKED_CAST")
    private fun processInput(input: Input): Flow<Outcome> =
        if (input is CancelInput<*>) emptyOutcomeFlow(input.showProgress)
                .also { cancellableInputsMap[input.clazz as KClass<I>] = AtomicBoolean(true) }
        else handleInputs(input as I, currentState)

    /**
     * Applies the Loading, Success & Error (LSE) pattern to every [Outcome]
     */
    private suspend fun processInputOutcomeStream(stream: InputOutcomeStream): Flow<Outcome> =
        stream.outcomes.catch { cause -> emit(ErrorOutcome(cause, stream.input)) } // wrap uncaught exceptions
            .flatMapConcat {
                if (it.showProgress) { // emit Progress true and false around the outcome
                    flowOf(it, ProgressOutcome(false, stream.input)).onEach { delay(DELAY) }
                        .onStart { emit(ProgressOutcome(true, stream.input)) }
                } else flowOf(it)
            }

    /**
     * Given a [Flow] of [Outcome]s return a [Flow] of [StateOutcome]s by filtering and applying the [reducer]
     * if exists (MVI vs MVVM).
     */
    @Suppress("UNCHECKED_CAST")
    private fun Flow<Outcome>.createStates(): Flow<StateOutcome<S>> =
        if (reducer == null) mapNotNull { if (it is StateOutcome<*>) it as StateOutcome<S> else null } // MVVM
        else mapNotNull { if (it is ResultOutcome<*>) it as ResultOutcome<R> else null } // MVI
            .scan(StateOutcome(currentState, false)) { state: StateOutcome<S>, result: ResultOutcome<R> ->
                StateOutcome(reducer.reduce(state.state, result.result), result.showProgress).also { log(result) }
            }

    /**
     * Passes and logs [Outcome]s to [viewModelListener] to be observed by the view
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun Outcome.handleOutcome(): Unit = when (this) {
        is ResultOutcome<*>, is EmptyOutcome -> Unit
        is ErrorOutcome -> viewModelListener.emit(error)
        is EffectOutcome<*> -> viewModelListener.emit(effect as Output)
        is ProgressOutcome -> viewModelListener.emit(progress)
        is StateOutcome<*> -> (state as S).takeIf { it != currentState }?.let { state ->
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
