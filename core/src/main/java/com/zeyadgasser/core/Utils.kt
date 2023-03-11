package com.zeyadgasser.core

import com.zeyadgasser.core.FluxViewModel.FluxEffect
import com.zeyadgasser.core.FluxViewModel.FluxResult
import com.zeyadgasser.core.FluxViewModel.FluxState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flowOf

class AsyncOutcomeFlow(val flow: Flow<FluxOutcome>) : Flow<FluxOutcome> {
    override suspend fun collect(collector: FlowCollector<FluxOutcome>) = Unit
}

sealed class FluxOutcome(open var input: Input = EmptyInput) : Loggable

object EmptyFluxOutcome : FluxOutcome()

data class FluxProgress(val progress: Progress) : FluxOutcome()

data class FluxError(var error: Error) : FluxOutcome() {
    override var input: Input = EmptyInput
        set(value) {
            error = error.copy(input = value)
            field = value
        }
}

fun Flow<FluxOutcome>.executeInParallel(): AsyncOutcomeFlow = AsyncOutcomeFlow(this)

fun <S : State> S.toStateOutcome(): FluxOutcome = FluxState(this)

fun <S : State> S.toStateOutcomeFlow(): Flow<FluxOutcome> = toStateOutcome().toFlow()

fun <E : Effect> E.toEffectOutcome(): FluxOutcome = FluxEffect(this)

fun <E : Effect> E.toEffectOutcomeFlow(): Flow<FluxOutcome> = toEffectOutcome().toFlow()

fun <R : Result> R.toResultOutcome(): FluxOutcome = FluxResult(this)

fun <R : Result> R.toResultOutcomeFlow(): Flow<FluxOutcome> = toResultOutcome().toFlow()

fun Throwable.toErrorOutcome(errorMessage: String? = null): FluxOutcome =
    FluxError(Error(errorMessage ?: message.orEmpty(), this))

fun Throwable.toErrorOutcomeFlow(): Flow<FluxOutcome> = toErrorOutcome().toFlow()

fun emptyOutcomeFlow() = EmptyFluxOutcome.toFlow()

fun FluxOutcome.toFlow() = flowOf(this)
