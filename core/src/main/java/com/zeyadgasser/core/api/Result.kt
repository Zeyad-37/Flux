package com.zeyadgasser.core.api

import com.zeyadgasser.core.Loggable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * All results defined must implement this interface, to be identifiable by the framework.
 */
interface Result : Loggable

fun Result.inFlow(): Flow<Result> = flowOf(this)
fun Result.inParallelFlow(): AsyncResultFlow = inFlow().inParallel()

fun emptyResultFlow(): Flow<EmptyResult> = flowOf(EmptyResult)

data object EmptyResult : Result
