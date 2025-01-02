package com.zeyadgasser.core.v1.api

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.v1.FluxViewModel.Companion.ARG_STATE_KEY
import com.zeyadgasser.core.v1.Outcome
import com.zeyadgasser.core.v1.Outcome.EmptyOutcome
import com.zeyadgasser.core.v1.Outcome.ErrorOutcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun emptyOutcome(showProgress: Boolean = false): EmptyOutcome = EmptyOutcome(showProgress)

fun emptyOutcomeFlow(showProgress: Boolean = false): Flow<EmptyOutcome> = flowOf(emptyOutcome(showProgress))


fun Flow<Outcome>.executeInParallel(): AsyncOutcomeFlow = AsyncOutcomeFlow(this)

inline fun <reified I : Input> cancelInput(showProgress: Boolean = true) = CancelInput(I::class, showProgress)

fun Throwable.toErrorOutcome(
    errorMessage: String? = null, input: Input = EmptyInput, showProgress: Boolean = true
): Outcome =
    ErrorOutcome(this, input, errorMessage, showProgress)

fun Throwable.toErrorOutcomeFlow(
    errorMessage: String? = null, input: Input = EmptyInput, showProgress: Boolean = true
): Flow<Outcome> = flowOf(toErrorOutcome(errorMessage, input, showProgress))

fun Throwable.toErrorOutcomeParallelFlow(
    errorMessage: String? = null,
    input: Input = EmptyInput,
    showProgress: Boolean
): AsyncOutcomeFlow =
    toErrorOutcomeFlow(errorMessage, input, showProgress).executeInParallel()

inline fun <reified T> SavedStateHandle.requireInitialState(): T =
    checkNotNull(get<T>(ARG_STATE_KEY)) { "Saved state with key: $ARG_STATE_KEY must be provided!" }
