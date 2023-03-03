package com.zeyadgasser.core

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadgasser.core.InputStrategy.DEBOUNCE
import com.zeyadgasser.core.InputStrategy.NONE
import com.zeyadgasser.core.InputStrategy.THROTTLE
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

@OptIn(FlowPreview::class)
abstract class FluxViewModel<I : Input, R : Result, S : State, E : Effect>(
    private var currentState: S,
    private val inputHandler: InputHandler<I, S>,
    private val reducer: Reducer<S, R>?,
    private val savedStateHandle: SavedStateHandle?,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
    private val logger: Logger = Logger.getLogger("flux logger"),
) : ViewModel() {

    internal data class FluxState<S>(val state: S) : FluxOutcome()
    internal data class FluxEffect<E>(val effect: E) : FluxOutcome()
    internal data class FluxResult<R>(val result: R) : FluxOutcome()

    private lateinit var job: Job

    private val viewModelListener: MutableStateFlow<Output> = MutableStateFlow(currentState)
    private val inputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val throttledInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val debouncedInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val loggingListener: LoggingListener<I, R, S, E> =
        LoggingListenerHelper<I, R, S, E>().also { log().invoke(it) }

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
        inputs { logger.log(Level.ALL, "${this@FluxViewModel::class.simpleName} - Input: $it") }
        progress { logger.log(Level.ALL, "${this@FluxViewModel::class.simpleName} - $it") }
        results { logger.log(Level.ALL, "${this@FluxViewModel::class.simpleName} - Result: $it") }
        effects { logger.log(Level.ALL, "${this@FluxViewModel::class.simpleName} - Effect: $it") }
        states { logger.log(Level.ALL, "${this@FluxViewModel::class.simpleName} - State: $it") }
        errors { logger.log(Level.ALL, "${this@FluxViewModel::class.simpleName} - $it") }
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
        } else outcome.filter { it is FluxState<*> }.map { it as FluxState<S> }
        val nonStates = outcome.filter { it !is FluxState<*> }.filter { it !is FluxResult<*> }
        job = viewModelScope.launch {
            merge(nonStates, states).onEach { handleOutcome(it) }.flowOn(ioDispatcher).collect()
        }
    }

    private fun createOutcomes(): Flow<FluxOutcome> =
        merge(inputs, throttledInputs.conflate(), debouncedInputs.debounce(DEBOUNCE.interval))
            .map {
                loggingListener.inputs(it)
                InputOutcomeStream(it, inputHandler.handleInputs(it, currentState))
            }.run {
                val asyncOutcomes = filter { it.outcomes is AsyncOutcomeFlow }
                    .map { it.copy(outcomes = (it.outcomes as AsyncOutcomeFlow).flow) }
                    .flatMapMerge { processInputOutcomeStream(it) }
                val sequentialOutcomes = filter { it.outcomes !is AsyncOutcomeFlow }
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
            is FluxProgress ->
                if (fluxOutcome.input.showProgress) viewModelListener.emit(fluxOutcome.progress)
            is FluxResult<*>, EmptyFluxOutcome -> Unit
        }
        if (fluxOutcome !is FluxProgress) {
            flow<Nothing> { // TODO improve
                delay(DELAY)
                Progress(false, fluxOutcome.input).let {
                    logOutcomes(FluxProgress(it))
                    viewModelListener.emit(it)
                }
            }.collect()
        }
    }

    override fun onCleared() = job.cancel()
}
