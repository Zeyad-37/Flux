package com.zeyadgasser.core.v2

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * View model that implements the MVI (Model View Intent) architecture pattern
 */
abstract class ViewModel<I : Input, R : Result, S : State, E : Effect>(
    initialState: S,
    private val reducer: Reducer<R, S>,
) : ViewModel() {

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)

    /**
     * Observe [state] in the view to react to state changes
     */
    val state: StateFlow<S> = _state

    private val _effect: MutableSharedFlow<E> = MutableSharedFlow()

    /**
     * Observe [effect] in the view to react to one time effects
     */
    val effect: Flow<E> = _effect

    private val jobs = mutableMapOf<I, Job>()


    private val inputs: MutableSharedFlow<Input> = MutableSharedFlow()
    private val throttledInputs: MutableSharedFlow<Input> = MutableSharedFlow()
    private val debouncedInputs: MutableSharedFlow<Input> = MutableSharedFlow()


    fun process(input: Input): Unit = viewModelScope.launch {
        when (input.inputStrategy) {
            NONE -> process(input)
            is Throttle -> delay(input.inputStrategy.interval).also { process(input) }
            is Debounce -> debouncedInputs.debounce { it.inputStrategy.interval }.map { process(input) } // todo review
        }
    }.let {}

    /**
     * Send inputs to be processed by the view model, they will be handled by [resolve] and then the [Result]'s will
     * be passed to the relevant flow
     *
     * @param input the input to be handled
     */
    private fun process(input: I) {
        log("Input", input)
        // Cancel any existing job for the same input
        if (input is CancelInput<*>) {
            jobs[input]?.cancel()?.also { log("Input Canceled!", input) }
        } else {
            jobs[input] = viewModelScope.launch(logUncaughtExceptions) {
                resolve(input, _state.value).onEach { result ->
                    when (result) {
                        is Effect -> {
                            log("Effect", result)
                            _effect.emit(result as E)
                        }

                        else -> {
                            _state.update { state ->
                                log("Result", result)
                                reducer.reduce(result as R, state).also { log("State", it) }
                            }
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    /**
     * Map [input]'s to [Result]'s
     *
     * @param state the current [State]
     * @param input the [Input] to be handled
     *
     * @return a [Flow] of [Result] representing the result of handling the input
     */
    protected abstract suspend fun resolve(input: I, state: S): Flow<Result>

    /**
     * [ViewModel] specific logger
     */
    private val tag: String = this::class.simpleName ?: "ViewModel"

    /**
     * Log [Input]'s and the resulting [Result]'s, [Effect]'s and [State]
     */
    private fun log(type: String, item: Any) = Log.d(tag, "$type : $item")

    /**
     * Log uncaught exceptions in [resolve]
     */
    private val logUncaughtExceptions = CoroutineExceptionHandler { _, throwable ->
        Log.e(tag, "Uncaught exception", throwable)
    }

    /**
     * Cancels the UDF when the ViewModel is destroyed.
     */
    final override fun onCleared(): Unit = viewModelScope.cancel()
}
