package com.zeyadgasser.core.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * A wrapper class to identify async flows vs sync flows
 */
class AsyncResultFlow(val flow: Flow<Result>) : Flow<Result> {
    override suspend fun collect(collector: FlowCollector<Result>) = Unit
}
