package com.zeyadgasser.core.api

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.FluxViewModel.Companion.ARG_STATE_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun Flow<Result>.inParallel(): AsyncResultFlow = AsyncResultFlow(this)

inline fun <reified I : Input> cancelInput(showProgress: Boolean = true) = CancelInput(I::class, showProgress)

fun Throwable.toErrorResult(errorMessage: String? = null, input: Input = EmptyInput): Result =
    Error(message ?: errorMessage.orEmpty(), this, input)

fun Throwable.toErrorResultFlow(errorMessage: String? = null, input: Input = EmptyInput): Flow<Result> =
    flowOf(toErrorResult(errorMessage, input))

fun Throwable.toErrorResultParallelFlow(errorMessage: String? = null, input: Input = EmptyInput): AsyncResultFlow =
    toErrorResultFlow(errorMessage, input).inParallel()

inline fun <reified T> SavedStateHandle.requireInitialState(): T =
    checkNotNull(get<T>(ARG_STATE_KEY)) { "Saved state with key: $ARG_STATE_KEY must be provided!" }
