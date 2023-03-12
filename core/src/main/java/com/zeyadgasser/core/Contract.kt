package com.zeyadgasser.core

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

sealed interface Loggable

open class Input(val showProgress: Boolean = true) : Loggable

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

data class Error(val message: String, val cause: Throwable, val input: Input? = null) : Output

interface Effect : Output {
    fun toEffectOutcome(): FluxOutcome = FluxViewModel.FluxEffect(this)
    fun toEffectOutcomeFlow(): Flow<FluxOutcome> = flowOf(toEffectOutcome())
}

data class Progress(val isLoading: Boolean, val input: Input) : Output

private const val THROTTLE_DEBOUNCE_INTERVAL = 370L

enum class InputStrategy(val interval: Long) {
    NONE(0L), THROTTLE(THROTTLE_DEBOUNCE_INTERVAL), DEBOUNCE(THROTTLE_DEBOUNCE_INTERVAL)
}

fun Flow<FluxOutcome>.executeInParallel(): AsyncOutcomeFlow = AsyncOutcomeFlow(this)

fun Throwable.toErrorOutcome(errorMessage: String? = null): FluxOutcome =
    FluxError(Error(errorMessage ?: message.orEmpty(), this))

fun Throwable.toErrorOutcomeFlow(errorMessage: String? = null): Flow<FluxOutcome> =
    flowOf(toErrorOutcome(errorMessage))
