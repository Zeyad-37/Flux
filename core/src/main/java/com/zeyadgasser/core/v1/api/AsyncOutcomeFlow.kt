package com.zeyadgasser.core.v1.api

import com.zeyadgasser.core.v1.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * A wrapper class to identify async flows vs sync flows
 */
class AsyncOutcomeFlow(val flow: Flow<Outcome>) : Flow<Outcome> {
    override suspend fun collect(collector: FlowCollector<Outcome>) = Unit
}
