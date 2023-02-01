package com.zeyadgasser.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun Flow<FluxOutcome>.executeInParallel(): AsyncOutcomeFlow = AsyncOutcomeFlow(this)

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
