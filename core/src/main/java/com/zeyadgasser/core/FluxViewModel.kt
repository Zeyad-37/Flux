@file:OptIn(FlowPreview::class)

package com.zeyadgasser.core

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.zeyadgasser.core.InputStrategy.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

const val ARG_STATE = "arg_state"

class AsyncOutcomeFlow(val Flow: Flow<FluxOutcome>) : Flow<FluxOutcome> {
    override suspend fun collect(collector: FlowCollector<FluxOutcome>) = Unit
}

data class InputOutcomeStream(val input: Input, val outcomes: Flow<FluxOutcome>)

internal object EmptyInput : Input()

sealed class FluxOutcome(open var input: Input = EmptyInput)

object EmptyFluxOutcome : FluxOutcome()

private data class FluxProgress(val progress: Progress) : FluxOutcome()

internal data class FluxError(var error: Error) : FluxOutcome() {
    override var input: Input = EmptyInput
        set(value) {
            error = error.copy(input = value)
            field = value
        }
}

abstract class FluxViewModel<I : Input, R : Result, S : State, E : Effect>(
    private val inputHandler: InputHandler<I, S>,
    private val reducer: Reducer<S, R>?,
    private val savedStateHandle: SavedStateHandle?,
) : ViewModel() {

    private data class FluxState<S>(val state: S) : FluxOutcome()
    internal data class FluxEffect<E>(val effect: E) : FluxOutcome()
    internal data class FluxResult<R>(val result: R) : FluxOutcome()

    private lateinit var currentState: S

    private var viewModelListener: MutableStateFlow<Output> = MutableStateFlow(currentState)

    private val inputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val throttledInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val debouncedInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val trackingListener: TrackingListener<I, R, S, E> = this.initTracking()
    private val loggingListener: LoggingListener<I, R, S, E> = this.initLogging()

    suspend fun bind(initialState: S): FluxViewModel<I, R, S, E> {
        currentState = savedStateHandle?.get(ARG_STATE) ?: initialState
        bindInputs()
        return this
    }

    fun observe(): StateFlow<Output> = viewModelListener

    /**
     * Input source provider. By default it returns empty
     * It can be overwritten to provide other inputs into the stream
     */
    open fun inputSource(): Flow<I> = emptyFlow()

    suspend fun process(input: I, inputStrategy: InputStrategy = NONE) = when (inputStrategy) {
        NONE -> inputs
        THROTTLE -> throttledInputs
        DEBOUNCE -> debouncedInputs
    }.emit(input)

    open fun log(): LoggingListenerHelper<I, R, S, E>.() -> Unit = {
        inputs { Log.d(this@FluxViewModel::class.simpleName, " - Input: $it") }
        progress { Log.d(this@FluxViewModel::class.simpleName, " - Progress: $it") }
        results { Log.d(this@FluxViewModel::class.simpleName, " - Result: $it") }
        effects { Log.d(this@FluxViewModel::class.simpleName, " - Effect: $it") }
        states { Log.d(this@FluxViewModel::class.simpleName, " - State: $it") }
    }

    open fun track(): TrackingListenerHelper<I, R, S, E>.() -> Unit = { /*empty*/ }

    private suspend fun bindInputs() {// TODO review here
        val outcome = createOutcomes()
        val stateResult = if (reducer != null) {
            outcome.filter { it is FluxResult<*> }.map { it as FluxResult<R> }
                .scan(FluxState(currentState)) { state: FluxState<S>, result: FluxResult<R> ->
                    FluxState(reducer.reduce(state.state, result.result))
                        .apply { input = result.input }
                }.onEach {
                    trackState(it.state, it.input as I)
                    logState(it.state)
                }
        } else {
            outcome.filter { it is FluxResult<*> }.map { it as FluxResult<R> }
        }
        merge(outcome.filter { it !is FluxResult<*> }, stateResult)
            .collect {
                trackEvents(it)
                logEvents(it)
                handleResult(it)
            }
    }

    private fun createOutcomes(): Flow<FluxOutcome> {
        val streamsToProcess = merge(
            inputSource(),
            inputs,// throttledInputs.throttle(THROTTLE.interval),
            debouncedInputs.debounce(DEBOUNCE.interval)
        ).flowOn(Dispatchers.IO)
            .onEach {
                trackInput(it)
                logInput(it)
            }.map { InputOutcomeStream(it, inputHandler.handleInputs(it, currentState)) }

        val asyncOutcomes = streamsToProcess.filter { it.outcomes is AsyncOutcomeFlow }
            .map { it.copy(outcomes = (it.outcomes as AsyncOutcomeFlow).Flow) }
            .flatMapMerge { processInputOutcomeStream(it) }
        val sequentialOutcomes = streamsToProcess.filter { it.outcomes !is AsyncOutcomeFlow }
            .flatMapConcat { processInputOutcomeStream(it) }
        return merge(asyncOutcomes, sequentialOutcomes)
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

    private fun trackEvents(event: FluxOutcome) = when (event) {
        is FluxProgress -> trackingListener.progress(event.progress)
        is FluxEffect<*> -> trackingListener.effects(event.effect as E, event.input as I)
        is FluxError -> trackingListener.errors(event.error)
        is FluxResult<*> -> trackingListener.results(event.result as R, event.input as I)
        is FluxState<*>, EmptyFluxOutcome -> Unit
    }

    private fun logEvents(event: FluxOutcome) = when (event) {
        is FluxProgress -> loggingListener.progress(event.progress)
        is FluxEffect<*> -> loggingListener.effects(event.effect as E)
        is FluxError -> loggingListener.errors(event.error)
        is FluxResult<*> -> loggingListener.results(event.result as R)
        is FluxState<*>, EmptyFluxOutcome -> Unit
    }

    private fun trackInput(input: I) = trackingListener.inputs(input)

    private fun logInput(input: I) = loggingListener.inputs(input)

    private fun trackState(state: S, input: I) = trackingListener.states(state, input)

    private fun logState(state: S) = loggingListener.states(state)

    private fun createRxError(throwable: Throwable, input: I): FluxError =
        FluxError(Error(throwable.message.orEmpty(), throwable, input)).apply { this.input = input }

    private suspend fun handleResult(fluxOutcome: FluxOutcome) {
        if (fluxOutcome is FluxProgress) {
            viewModelListener.emit(fluxOutcome.progress)
        } else {
            if ((fluxOutcome.input as? I)?.showProgress == true)
                viewModelListener.emit(Progress(false, fluxOutcome.input))
        }
        when (fluxOutcome) {
            is FluxError -> viewModelListener.emit(fluxOutcome.error)
            is FluxEffect<*> -> viewModelListener.emit(fluxOutcome.effect as E)
            is FluxState<*> -> {
                savedStateHandle?.set(ARG_STATE, fluxOutcome.state as S) ?: Unit
                currentState = fluxOutcome.state as S
                viewModelListener.emit(fluxOutcome.state)
            }
            is FluxResult<*>, is FluxProgress, EmptyFluxOutcome -> Unit
        }
    }

    private fun initTracking(): TrackingListener<I, R, S, E> {
        val trackingListenerHelper = TrackingListenerHelper<I, R, S, E>()
        val init: TrackingListenerHelper<I, R, S, E>.() -> Unit = track()
        trackingListenerHelper.init()
        return trackingListenerHelper
    }

    private fun initLogging(): LoggingListener<I, R, S, E> {
        val loggingListenerHelper = LoggingListenerHelper<I, R, S, E>()
        val init: LoggingListenerHelper<I, R, S, E>.() -> Unit = log()
        loggingListenerHelper.init()
        return loggingListenerHelper
    }
}
