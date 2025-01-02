package com.zeyadgasser.core.v1.api

import com.zeyadgasser.core.v1.FluxViewModel.EffectOutcome
import com.zeyadgasser.core.v1.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * All effects presented to the view and produced by the VM must implement this [Effect] interface.
 */
interface Effect : Output {
    fun toEffectOutcome(showProgress: Boolean = true): Outcome = EffectOutcome(this, showProgress)
    fun toEffectOutcomeFlow(showProgress: Boolean = true): Flow<Outcome> = flowOf(toEffectOutcome(showProgress))
    fun toEffectOutcomeParallelFlow(showProgress: Boolean = true): AsyncOutcomeFlow =
        toEffectOutcomeFlow(showProgress).executeInParallel()
}
