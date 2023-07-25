package com.zeyadgasser.core.api

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.FluxViewModel.Companion.ARG_STATE_KEY
import com.zeyadgasser.core.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun Flow<Outcome>.executeInParallel(): AsyncOutcomeFlow = AsyncOutcomeFlow(this)

fun Throwable.toErrorOutcome(errorMessage: String? = null, input: Input = EmptyInput): Outcome =
    Outcome.ErrorOutcome(this, errorMessage, input)

fun Throwable.toErrorOutcomeFlow(errorMessage: String? = null, input: Input = EmptyInput): Flow<Outcome> =
    flowOf(toErrorOutcome(errorMessage, input))

inline fun <reified T> SavedStateHandle.requireInitialState(): T =
    checkNotNull(get<T>(ARG_STATE_KEY)) { "Saved state with key: $ARG_STATE_KEY must be provided!" }
