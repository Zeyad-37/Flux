@file:OptIn(FlowPreview::class)

package com.zeyadgasser.core

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadgasser.core.InputStrategy.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

abstract class FluxViewModel<I : Input, R : Result, S : State, E : Effect>(
    private var currentState: S,
    private val inputHandler: InputHandler<I, S>,
    private val reducer: Reducer<S, R>?,
    private val savedStateHandle: SavedStateHandle?,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : ViewModel() {

    internal data class FluxState<S>(val state: S) : FluxOutcome()
    internal data class FluxEffect<E>(val effect: E) : FluxOutcome()
    internal data class FluxResult<R>(val result: R) : FluxOutcome()

    private var job: Job? = null
    private val viewModelListener: MutableStateFlow<Output> = MutableStateFlow(currentState)
    private val inputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val throttledInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val debouncedInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val loggingListener: LoggingListener<I, R, S, E> =
        LoggingListenerHelper<I, R, S, E>().apply { log().invoke(this) }

    init {
        bindInputs()
    }

    fun observe(): StateFlow<Output> = viewModelListener.asStateFlow()

    fun process(input: I, inputStrategy: InputStrategy = NONE) = viewModelScope.launch {
        when (inputStrategy) {
            NONE -> inputs
            THROTTLE -> throttledInputs
            DEBOUNCE -> debouncedInputs
        }.emit(input)
    }.let {}

    open fun log(): LoggingListenerHelper<I, R, S, E>.() -> Unit = {
        inputs { Log.d(this@FluxViewModel::class.simpleName, " - Input: $it") }
        progress { Log.d(this@FluxViewModel::class.simpleName, " - $it") }
        results { Log.d(this@FluxViewModel::class.simpleName, " - Result: $it") }
        effects { Log.d(this@FluxViewModel::class.simpleName, " - Effect: $it") }
        states { Log.d(this@FluxViewModel::class.simpleName, " - State: $it") }
        errors { Log.d(this@FluxViewModel::class.simpleName, " - $it") }
    }

    private fun bindInputs() {
        val outcome: Flow<FluxOutcome> = createOutcomes()
        val states: Flow<FluxState<S>> = if (reducer != null) {
            outcome.filter { it is FluxResult<*> }
                .map { it as FluxResult<R> }
                .scan(FluxState(currentState)) { state: FluxState<S>, result: FluxResult<R> ->
                    FluxState(reducer.reduce(state.state, result.result))
                        .apply { input = result.input }
                }
        } else {
            outcome.filter { it is FluxState<*> }.map { it as FluxState<S> }
        }
        val nonStateOutcomes =
            outcome.filter { it !is FluxState<*> }.filter { it !is FluxResult<*> }
        val outcomeFlow = merge(nonStateOutcomes, states)
            .onEach { handleOutcome(it) }
            .flowOn(ioDispatcher)
        job = viewModelScope.launch { outcomeFlow.collect {} }
    }

    private fun createOutcomes(): Flow<FluxOutcome> =
        merge(inputs, throttledInputs.conflate(), debouncedInputs.debounce(DEBOUNCE.interval))
            .map {
                loggingListener.inputs(it)
                InputOutcomeStream(it, inputHandler.handleInputs(it, currentState))
            }.run {
                val asyncOutcomes = this.filter { it.outcomes is AsyncOutcomeFlow }
                    .map { it.copy(outcomes = (it.outcomes as AsyncOutcomeFlow).Flow) }
                    .flatMapMerge { processInputOutcomeStream(it) }
                val sequentialOutcomes = this.filter { it.outcomes !is AsyncOutcomeFlow }
                    .flatMapConcat { processInputOutcomeStream(it) }
                merge(asyncOutcomes, sequentialOutcomes)
            }

    private fun processInputOutcomeStream(inputOutcomeStream: InputOutcomeStream): Flow<FluxOutcome> {
        val result = inputOutcomeStream.outcomes
            .map { it.apply { input = inputOutcomeStream.input } }
            .catch { emit(createRxError(it, inputOutcomeStream.input as I)) }
        return if (inputOutcomeStream.input.showProgress) {
            result.onStart { emit(FluxProgress(Progress(true, inputOutcomeStream.input))) }
        } else {
            result
        }
    }

    private fun createRxError(throwable: Throwable, input: I): FluxError =
        FluxError(Error(throwable.message.orEmpty(), throwable, input)).apply { this.input = input }

    private fun logOutcomes(outcome: FluxOutcome) = when (outcome) {
        is FluxError -> loggingListener.errors(outcome.error)
        is FluxProgress -> loggingListener.progress(outcome.progress)
        is FluxState<*> -> loggingListener.states(outcome.state as S)
        is FluxEffect<*> -> loggingListener.effects(outcome.effect as E)
        is FluxResult<*> -> loggingListener.results(outcome.result as R)
        EmptyFluxOutcome -> Unit
    }

    private suspend fun handleOutcome(fluxOutcome: FluxOutcome) {
        logOutcomes(fluxOutcome)
        when (fluxOutcome) {
            is FluxError -> viewModelListener.emit(fluxOutcome.error)
            is FluxEffect<*> -> viewModelListener.emit(fluxOutcome.effect as E)
            is FluxState<*> -> (fluxOutcome.state as S).let { state ->
                savedStateHandle?.set(ARG_STATE, state)
                currentState = state
                viewModelListener.emit(state)
            }
            is FluxProgress -> if (fluxOutcome.input.showProgress) {
                viewModelListener.emit(fluxOutcome.progress)
            }
            is FluxResult<*>, EmptyFluxOutcome -> Unit
        }
        if (fluxOutcome !is FluxProgress) {
            flow<Nothing> {// TODO improve
                delay(10)
                Progress(false, fluxOutcome.input).let {
                    logOutcomes(FluxProgress(it))
                    viewModelListener.emit(it)
                }
            }.collect()
        }
    }

    override fun onCleared() = job?.cancel() ?: Unit
}
