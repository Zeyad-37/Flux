package com.zeyadgasser.core

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlin.properties.Delegates.observable

const val ARG_STATE = "arg_state"

class AsyncOutcomeFlow(val Flow: Flow<FluxOutcome>) : Flow<FluxOutcome> {
    override suspend fun collect(collector: FlowCollector<FluxOutcome>) = Unit
}

data class InputOutcomeStream(val input: Input, val outcomes: Flow<FluxOutcome>)

internal object EmptyInput : Input()

open class FluxOutcome(open var input: Input = EmptyInput)

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

    private lateinit var disposable: Job
    private lateinit var currentState: S

    private var viewModelListener: ViewModelListener<S, E>? = null
        set(value) {
            value?.states?.invoke(currentState)
            field = value
        }
    private var progress: Progress by observable(
        Progress(false, EmptyInput)
    ) { _, oldValue, newValue ->
        if (newValue != oldValue) notifyProgressChanged(newValue)
    }

    private val inputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val throttledInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val debouncedInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val trackingListener: TrackingListener<I, R, S, E> = this.initTracking()
    private val loggingListener: LoggingListener<I, R, S, E> = this.initLogging()

    suspend fun bind(
        initialState: S,
        inputs: () -> MutableSharedFlow<I>
    ): FluxViewModel<I, R, S, E> {
        currentState = savedStateHandle?.get(ARG_STATE) ?: initialState
        bindInputs(inputs)
        return this
    }

    fun observe(lifecycleOwner: LifecycleOwner, init: ViewModelListenerHelper<S, E>.() -> Unit) {
        val helper = ViewModelListenerHelper<S, E>()
        helper.init()
        viewModelListener = helper
        removeObservers(lifecycleOwner)
    }

    /**
     * Input source provider. By default it returns empty
     * It can be overwritten to provide other inputs into the stream
     */
    open fun inputSource(): Flow<I> = emptyFlow()

    suspend fun process(input: I, inputStrategy: InputStrategy = InputStrategy.NONE) =
        when (inputStrategy) {
            InputStrategy.NONE -> inputs
            InputStrategy.THROTTLE -> throttledInputs
            InputStrategy.DEBOUNCE -> debouncedInputs
        }.emit(input)

    open fun log(): LoggingListenerHelper<I, R, S, E>.() -> Unit = {
        inputs { Log.d(this@FluxViewModel::class.simpleName, " - Input: $it") }
        progress { Log.d(this@FluxViewModel::class.simpleName, " - Progress: $it") }
        results { Log.d(this@FluxViewModel::class.simpleName, " - Result: $it") }
        effects { Log.d(this@FluxViewModel::class.simpleName, " - Effect: $it") }
        states { Log.d(this@FluxViewModel::class.simpleName, " - State: $it") }
    }

    open fun track(): TrackingListenerHelper<I, R, S, E>.() -> Unit = { /*empty*/ }

    private suspend fun bindInputs(inputs: () -> MutableSharedFlow<I>) {
        val outcome = createOutcomes(inputs)
        val stateResult = if (reducer != null) {
            outcome.filter { it is FluxResult<*> }.map { it as FluxResult<R> }
                .scan(FluxState(currentState)) { state: FluxState<S>, result: FluxResult<R> ->
                    FluxState(reducer.reduce(state.state, result.result)).apply {
                        input = result.input
                    }
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

    private fun createOutcomes(inputs: () -> MutableSharedFlow<I>): Flow<FluxOutcome> {
        val streamsToProcess = merge(
            inputs(),
            inputSource(),
            this.inputs,// throttledInputs.throttle(InputStrategy.THROTTLE.interval),
            debouncedInputs.debounce(InputStrategy.DEBOUNCE.interval)
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
        return if (inputOutcomeStream.input.showProgress.not()) {
            result
        } else {
            result.onStart { emit(FluxProgress(Progress(true, inputOutcomeStream.input))) }
        }
    }

    private fun trackEvents(event: FluxOutcome) {
        when (event) {
            is FluxProgress -> trackingListener.progress(event.progress)
            is FluxEffect<*> -> trackingListener.effects(event.effect as E, event.input as I)
            is FluxError -> trackingListener.errors(event.error)
            is FluxResult<*> -> trackingListener.results(event.result as R, event.input as I)
        }
    }

    private fun logEvents(event: FluxOutcome) {
        when (event) {
            is FluxProgress -> loggingListener.progress(event.progress)
            is FluxEffect<*> -> loggingListener.effects(event.effect as E)
            is FluxError -> loggingListener.errors(event.error)
            is FluxResult<*> -> loggingListener.results(event.result as R)
        }
    }

    private fun trackInput(input: I) = trackingListener.inputs(input)

    private fun logInput(input: I) = loggingListener.inputs(input)

    private fun trackState(state: S, input: I) = trackingListener.states(state, input)

    private fun logState(state: S) = loggingListener.states(state)

    private fun createRxError(throwable: Throwable, input: I): FluxError =
        FluxError(Error(throwable.message.orEmpty(), throwable, input)).apply { this.input = input }

    private fun handleResult(result: FluxOutcome) {
        if (result is FluxProgress) {
            notifyProgressChanged(result.progress)
        } else {
            dismissProgressDependingOnInput(result.input as I)
        }
        when (result) {
            is FluxError -> notifyError(result.error)
            is FluxEffect<*> -> notifyEffect(result.effect as E)
            is FluxState<*> -> {
                saveState(result.state as S)
                notifyNewState(result.state)
            }
            else ->  Unit
        }
    }

    private fun dismissProgressDependingOnInput(input: I?) {
        if (input?.showProgress == true) {
            notifyProgressChanged(Progress(false, input))
        }
    }

    private fun notifyProgressChanged(progress: Progress) =
        viewModelListener?.progress?.invoke(progress)

    private fun notifyEffect(effect: E) = viewModelListener?.effects?.invoke(effect)

    private fun notifyError(error: Error) = viewModelListener?.errors?.invoke(error)

    private fun notifyNewState(state: S) {
        currentState = state
        viewModelListener?.states?.invoke(state)
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

    private fun saveState(state: S) = savedStateHandle?.set(ARG_STATE, state) ?: Unit

    private fun removeObservers(lifecycleOwner: LifecycleOwner) =
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                unBind()
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        })

    private fun unBind() {
        viewModelListener = null
        disposable.cancel()
    }

    override fun onCleared() = unBind()
}
