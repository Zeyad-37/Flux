package com.zeyadgasser.core

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
abstract class FluxViewModel<I : Input, R : Result, S : State, E : Effect>(
    val initialState: S,
    private val savedStateHandle: SavedStateHandle? = null,
    private val reducer: Reducer<S, R>? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    companion object {
        const val ARG_STATE_KEY = "arg_state_key"
        private const val DELAY = 100L
    }

    data class FluxEffect<E>(val effect: E) : FluxOutcome()
    data class FluxResult<R>(val result: R) : FluxOutcome()
    data class FluxState<S>(
        val state: S, override var input: Input = EmptyInput,
    ) : FluxOutcome(input)

    private data class InputOutcomeStream(val input: Input, val outcomes: Flow<FluxOutcome>)

    private lateinit var job: Job
    private var currentState: S = initialState

    private val tag = this::class.simpleName
    private val inputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val throttledInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val debouncedInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val viewModelListener: MutableStateFlow<Output> = MutableStateFlow(currentState)

    init {
        activate()
    }

    /**
     * Call observe from view to listen to state and effect updates.
     */
    fun observe(): StateFlow<Output> = viewModelListener.asStateFlow()

    /**
     * Send inputs: I to be processed by ViewModel.
     */
    fun process(input: I): Unit = viewModelScope.launch {
        when (input.inputStrategy) {
            NONE -> inputs
            is Throttle -> throttledInputs
            is Debounce -> debouncedInputs
        }.emit(input)
    }.let {}

    /**
     * Map inputs: I with current state: S to a Flow of FluxOutcomes.
     */
    protected abstract fun handleInputs(input: I, currentState: S): Flow<FluxOutcome>

    /**
     * Override to implement with your preferred logger.
     */
    protected open fun log(loggable: Loggable): Unit = Log.d("$tag", "$loggable").let { }

    private fun activate(): Unit = createOutcomes().let { outcomeFlow ->
        val states = createStates(outcomeFlow, reducer)
        val nonStates = outcomeFlow.filter { it !is FluxState<*> && it !is FluxResult<*> }
        job = viewModelScope.launch(dispatcher) { merge(nonStates, states).collect { it.handleOutcome() } }
    }

    private fun createOutcomes(): Flow<FluxOutcome> = merge(
        inputs,
        throttledInputs.onEach { delay(it.inputStrategy.interval) },
        debouncedInputs.debounce { it.inputStrategy.interval }
    ).map { input ->
        log(input)
        InputOutcomeStream(input, handleInputs(input, currentState))
    }.run {
        val asyncOutcomes = filter { it.outcomes is AsyncOutcomeFlow }
            .map { it.copy(outcomes = (it.outcomes as AsyncOutcomeFlow).flow) }
            .flatMapMerge { processInputOutcomeStream(it) }
        val sequentialOutcomes = filter { it.outcomes !is AsyncOutcomeFlow }
            .flatMapConcat { processInputOutcomeStream(it) }
        merge(asyncOutcomes, sequentialOutcomes)
    }

    private fun processInputOutcomeStream(stream: InputOutcomeStream): Flow<FluxOutcome> {
        val fluxOutcomeFlow = stream.outcomes
            .map { fluxOutcome -> fluxOutcome.apply { input = stream.input } }
            .catch { cause -> emit(FluxError(cause, input = stream.input)) }
        return if (stream.input.showProgress) {
            fluxOutcomeFlow.flatMapConcat {
                flowOf(it, FluxProgress(false, it.input)).onEach { delay(DELAY) }
            }.onStart { emit(FluxProgress(true, stream.input)) }
        } else fluxOutcomeFlow
    }

    private fun createStates(outcome: Flow<FluxOutcome>, reducer: Reducer<S, R>?): Flow<FluxState<S>> =
        if (reducer == null)
            outcome.mapNotNull { if (it is FluxState<*>) it as FluxState<S> else null } // MVVM
        else
            outcome.mapNotNull { if (it is FluxResult<*>) it as FluxResult<R> else null } // MVI
                .scan(FluxState(currentState)) { state: FluxState<S>, result: FluxResult<R> ->
                    FluxState(reducer.reduce(state.state, result.result), result.input).also { log(result) }
                }

    private suspend fun FluxOutcome.handleOutcome(): Unit = when (this) {
        is FluxError -> viewModelListener.emit(error)
        is FluxEffect<*> -> viewModelListener.emit(effect as Output)
        is FluxProgress -> viewModelListener.emit(progress)
        is FluxState<*> -> (state as S).takeIf { it != currentState }?.let { state ->
            savedStateHandle?.set(ARG_STATE_KEY, state)
            currentState = state
            viewModelListener.emit(state)
        } ?: Unit
        is FluxResult<*>, EmptyFluxOutcome -> Unit
    }.also { log(this) }

    final override fun onCleared(): Unit = job.cancel()
}
