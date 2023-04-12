package com.zeyadgasser.core

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flowOf

sealed interface Loggable

open class Input(
    val showProgress: Boolean = true, val inputStrategy: InputStrategy = NONE,
) : Loggable {
    override fun toString() =
        "${this::class.simpleName}(showProgress=$showProgress, inputStrategy=${inputStrategy}ms)"
}

object EmptyInput : Input()

interface Result {
    fun toResultOutcome(): FluxOutcome = FluxViewModel.FluxResult(this)
    fun toResultOutcomeFlow(): Flow<FluxOutcome> = flowOf(toResultOutcome())
}

sealed interface Output

interface State : Parcelable, Output {
    fun toStateOutcome(): FluxOutcome = FluxViewModel.FluxState(this)
    fun toStateOutcomeFlow(): Flow<FluxOutcome> = flowOf(toStateOutcome())
}

data class Error internal constructor(
    val message: String, val cause: Throwable, val input: Input = EmptyInput,
) : Output

interface Effect : Output {
    fun toEffectOutcome(): FluxOutcome = FluxViewModel.FluxEffect(this)
    fun toEffectOutcomeFlow(): Flow<FluxOutcome> = flowOf(toEffectOutcome())
}

data class Progress constructor(val isLoading: Boolean, val input: Input) : Output

class AsyncOutcomeFlow(val flow: Flow<FluxOutcome>) : Flow<FluxOutcome> {
    override suspend fun collect(collector: FlowCollector<FluxOutcome>) = Unit
}

sealed class FluxOutcome(open var input: Input = EmptyInput) : Loggable

object EmptyFluxOutcome : FluxOutcome() {
    fun emptyOutcomeFlow() = flowOf(EmptyFluxOutcome)
}

internal data class FluxProgress(val progress: Progress) : FluxOutcome() {
    constructor(isLoading: Boolean, input: Input) : this(Progress(isLoading, input))
}

data class FluxError(val error: Error, override var input: Input = EmptyInput) : FluxOutcome(input) {
    constructor(cause: Throwable, errorMessage: String? = null, input: Input = EmptyInput) :
            this(Error(errorMessage ?: cause.message.orEmpty(), cause, input), input)
}

interface Reducer<S : State, R : Result> {
    fun reduce(state: S, result: R): S
}

private const val DEFAULT_INTERVAL = 370L

sealed class InputStrategy(val interval: Long = DEFAULT_INTERVAL)

object NONE : InputStrategy(0L)
data class Throttle(private val customInterval: Long = DEFAULT_INTERVAL) : InputStrategy(customInterval)

data class Debounce(private val customInterval: Long = DEFAULT_INTERVAL) : InputStrategy(customInterval)

fun Flow<FluxOutcome>.executeInParallel(): AsyncOutcomeFlow = AsyncOutcomeFlow(this)

fun Throwable.toErrorOutcome(errorMessage: String? = null): FluxOutcome = FluxError(this, errorMessage)

fun Throwable.toErrorOutcomeFlow(errorMessage: String? = null): Flow<FluxOutcome> =
    flowOf(toErrorOutcome(errorMessage))
