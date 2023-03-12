package com.zeyadgasser.core

import android.util.Log
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
import kotlinx.coroutines.flow.FlowCollector
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
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AsyncOutcomeFlow(val flow: Flow<FluxOutcome>) : Flow<FluxOutcome> {
    override suspend fun collect(collector: FlowCollector<FluxOutcome>) = Unit
}

sealed class FluxOutcome(open var input: Input = EmptyInput) : Loggable

object EmptyFluxOutcome : FluxOutcome() {
    fun emptyOutcomeFlow() = flowOf(EmptyFluxOutcome)
}

data class FluxProgress(val progress: Progress) : FluxOutcome()

data class FluxError(var error: Error, override var input: Input = EmptyInput) : FluxOutcome(input)

interface Reducer<S : State, R : Result> {
    fun reduce(state: S, result: R): S
}

@OptIn(FlowPreview::class)
abstract class FluxViewModel<I : Input, R : Result, S : State, E : Effect> @JvmOverloads constructor(
    val initialState: S,
    private val savedStateHandle: SavedStateHandle? = null,
    private val reducer: Reducer<S, R>? = null,
    private val bgDispatcher: CoroutineContext = Dispatchers.IO,
) : ViewModel() {

    companion object {
        const val ARG_STATE_KEY = "arg_state_key"
        private const val DELAY = 50L
    }

    data class FluxEffect<E>(val effect: E) : FluxOutcome()
    data class FluxResult<R>(val result: R) : FluxOutcome()
    data class FluxState<S>(
        val state: S, override var input: Input = EmptyInput
    ) : FluxOutcome(input)

    private data class InputOutcomeStream(val input: Input, val outcomes: Flow<FluxOutcome>)

    private lateinit var job: Job
    private var currentState: S = initialState

    private val viewModelListener: MutableStateFlow<Output> = MutableStateFlow(currentState)
    private val inputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val throttledInputs: MutableSharedFlow<I> = MutableSharedFlow()
    private val debouncedInputs: MutableSharedFlow<I> = MutableSharedFlow()

    init {
        activate()
    }

    fun observe(): StateFlow<Output> = viewModelListener.asStateFlow()

    fun process(input: I, inputStrategy: InputStrategy = NONE) = viewModelScope.launch {
        when (inputStrategy) {
            NONE -> inputs
            THROTTLE -> throttledInputs//.onEach { delay(THROTTLE.interval) }.let { throttledInputs }
            DEBOUNCE -> debouncedInputs//.debounce(DEBOUNCE.interval).let { debouncedInputs }
        }.emit(input)
    }.let {}

    protected abstract fun handleInputs(input: I, currentState: S): Flow<FluxOutcome>

    protected open fun log(loggable: Loggable) = when (loggable) {
        EmptyFluxOutcome -> Log.d("${this::class.simpleName}", " - EmptyFluxOutcome")
        is Input -> Log.d("${this::class.simpleName}", " - Input: $loggable")
        is FluxEffect<*> -> Log.d("${this::class.simpleName}", " - Effect: $loggable")
        is FluxError -> Log.d("${this::class.simpleName}", " - $loggable")
        is FluxProgress -> Log.d("${this::class.simpleName}", " - $loggable")
        is FluxResult<*> -> Log.d("${this::class.simpleName}", " - Result: $loggable")
        is FluxState<*> -> Log.d("${this::class.simpleName}", " - State: $loggable")
    }.let {}

    private fun activate() {
        val outcome = createOutcomes()
        val states = createStates(outcome, reducer)
        val nonStates = outcome.filter { it !is FluxState<*> && it !is FluxResult<*> }
        job = viewModelScope
            .launch(bgDispatcher) { merge(nonStates, states).collect { it.handleOutcome() } }
    }

    private fun createOutcomes(): Flow<FluxOutcome> = merge(
        inputs,
        throttledInputs.onEach { delay(THROTTLE.interval) },
        debouncedInputs.debounce(DEBOUNCE.interval)
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

    private fun createStates(
        outcome: Flow<FluxOutcome>, reducer: Reducer<S, R>?
    ): Flow<FluxState<S>> = if (reducer == null) { // MVVM
        outcome.filter { it is FluxState<*> }.map { it as FluxState<S> }
    } else { // MVI
        outcome.filter { it is FluxResult<*> }.map { it as FluxResult<R> }
            .scan(FluxState(currentState)) { state: FluxState<S>, result: FluxResult<R> ->
                log(result)
                FluxState(reducer.reduce(state.state, result.result), result.input)
            }
    }

    private fun processInputOutcomeStream(stream: InputOutcomeStream): Flow<FluxOutcome> {
        val fluxOutcomeFlow = stream.outcomes.map { fluxOutcome: FluxOutcome ->
            fluxOutcome.apply {
                input = stream.input
            }
        }.catch { cause: Throwable ->
            emit(FluxError(Error(cause.message.orEmpty(), cause, stream.input), stream.input))
        }
        return if (stream.input.showProgress) {
            fluxOutcomeFlow.flatMapConcat {
                flowOf(it, FluxProgress(Progress(false, it.input))).onEach { delay(DELAY) }
            }.onStart { emit(FluxProgress(Progress(true, stream.input))) }
        } else fluxOutcomeFlow
    }

    private suspend fun FluxOutcome.handleOutcome() = when (this) {
        is FluxError -> viewModelListener.emit(error)
        is FluxEffect<*> -> viewModelListener.emit(effect as Output)
        is FluxProgress -> viewModelListener.emit(progress)
        is FluxState<*> -> (state as S).takeIf { it != currentState }?.let { state ->
            savedStateHandle?.set(ARG_STATE_KEY, state)
            currentState = state
            viewModelListener.emit(state)
        }
        is FluxResult<*>, EmptyFluxOutcome -> Unit
    }.also { log(this) }

    override fun onCleared() = job.cancel()
}
