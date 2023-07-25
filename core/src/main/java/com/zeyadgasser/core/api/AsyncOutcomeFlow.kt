package com.zeyadgasser.core.api

import com.zeyadgasser.core.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * A wrapper class to identify async flows vs sync flows
 */
class AsyncOutcomeFlow(val flow: Flow<Outcome>) : Flow<Outcome> {
    override suspend fun collect(collector: FlowCollector<Outcome>) = Unit
}
