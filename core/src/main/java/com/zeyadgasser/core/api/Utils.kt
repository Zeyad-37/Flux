package com.zeyadgasser.core.api

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.FluxViewModel.Companion.ARG_STATE_KEY
import com.zeyadgasser.core.Outcome
import com.zeyadgasser.core.Outcome.ErrorOutcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun Flow<Outcome>.executeInParallel(): AsyncOutcomeFlow = AsyncOutcomeFlow(this)

inline fun <reified I : Input> cancelInput(showProgress: Boolean = true) = CancelInput(I::class, showProgress)

fun Throwable.toErrorOutcome(errorMessage: String? = null, input: Input = EmptyInput): Outcome =
    ErrorOutcome(this, errorMessage, input)

fun Throwable.toErrorOutcomeFlow(errorMessage: String? = null, input: Input = EmptyInput): Flow<Outcome> =
    flowOf(toErrorOutcome(errorMessage, input))

fun Throwable.toErrorOutcomeParallelFlow(errorMessage: String? = null, input: Input = EmptyInput): AsyncOutcomeFlow =
    toErrorOutcomeFlow(errorMessage, input).executeInParallel()

inline fun <reified T> SavedStateHandle.requireInitialState(): T =
    checkNotNull(get<T>(ARG_STATE_KEY)) { "Saved state with key: $ARG_STATE_KEY must be provided!" }
