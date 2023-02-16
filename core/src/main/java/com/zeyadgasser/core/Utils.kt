package com.zeyadgasser.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flowOf

const val ARG_STATE = "arg_state"

class AsyncOutcomeFlow(val Flow: Flow<FluxOutcome>) : Flow<FluxOutcome> {
    override suspend fun collect(collector: FlowCollector<FluxOutcome>) = Unit
}

data class InputOutcomeStream(val input: Input, val outcomes: Flow<FluxOutcome>)

internal object EmptyInput : Input()

sealed class FluxOutcome(open var input: Input = EmptyInput)

object EmptyFluxOutcome : FluxOutcome()

data class FluxProgress(val progress: Progress) : FluxOutcome()

internal data class FluxError(var error: Error) : FluxOutcome() {
    override var input: Input = EmptyInput
        set(value) {
            error = error.copy(input = value)
            field = value
        }
}

fun Flow<FluxOutcome>.executeInParallel(): AsyncOutcomeFlow = AsyncOutcomeFlow(this)

fun <S : State> S.toStateOutcome(): FluxOutcome = FluxViewModel.FluxState(this)

fun <S : State> S.toStateOutcomeFlow(): Flow<FluxOutcome> = toStateOutcome().toFlow()

fun <E : Effect> E.toEffectOutcome(): FluxOutcome = FluxViewModel.FluxEffect(this)

fun <E : Effect> E.toEffectOutcomeFlow(): Flow<FluxOutcome> = toEffectOutcome().toFlow()

fun <R : Result> R.toResultOutcome(): FluxOutcome = FluxViewModel.FluxResult(this)

fun <R : Result> R.toResultOutcomeFlow(): Flow<FluxOutcome> = toResultOutcome().toFlow()

fun Throwable.toErrorOutcome(errorMessage: String? = null): FluxOutcome =
    FluxError(Error(errorMessage ?: message.orEmpty(), this))

fun Throwable.toErrorOutcomeFlow(): Flow<FluxOutcome> = toErrorOutcome().toFlow()

fun emptyOutcome() = EmptyFluxOutcome

fun emptyOutcomeFlow() = EmptyFluxOutcome.toFlow()

fun FluxOutcome.toFlow() = flowOf(this)
