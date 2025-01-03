package com.zeyadgasser.core.v1.api

import com.zeyadgasser.core.v1.FluxViewModel.ResultOutcome
import com.zeyadgasser.core.v1.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * All results defined must implement this interface, to be identifiable by the framework.
 */
interface Result {
    fun toResultOutcome(showProgress: Boolean = true): Outcome = ResultOutcome(this, showProgress)
    fun toResultOutcomeFlow(showProgress: Boolean = true): Flow<Outcome> = flowOf(toResultOutcome(showProgress))
    fun toResultOutcomeParallelFlow(showProgress: Boolean = true): AsyncOutcomeFlow =
        toResultOutcomeFlow(showProgress).executeInParallel()
}
