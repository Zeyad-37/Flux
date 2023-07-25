package com.zeyadgasser.core.api

import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * All results defined must implement this interface, to be identifiable by the framework.
 */
interface Result {
    fun toResultOutcome(): Outcome = FluxViewModel.ResultOutcome(this)
    fun toResultOutcomeFlow(): Flow<Outcome> = flowOf(toResultOutcome())
}
