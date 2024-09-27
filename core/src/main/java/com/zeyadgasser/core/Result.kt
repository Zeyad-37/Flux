package com.zeyadgasser.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * All results defined must implement this interface, to be identifiable by the framework.
 */
interface Result : Loggable

data object EmptyResult : Result

/**
 * A wrapper class to identify async flows vs sync flows
 */
class AsyncResultFlow(val flow: Flow<Result>) : Flow<Result> {
    override suspend fun collect(collector: FlowCollector<Result>) = Unit
}

/**
 * Wrapper class to attribute inputs and [Result]s as a pair. A key value pair basically.
 */
internal data class InputResultFlowPair(val input: Input, val resultFlow: Flow<Result>)
