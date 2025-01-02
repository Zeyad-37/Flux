package com.zeyadgasser.core.v1.api

import android.os.Parcelable
import com.zeyadgasser.core.v1.FluxViewModel.StateOutcome
import com.zeyadgasser.core.v1.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * All states presented to the view and produced/reduced by the VM must implement this [State] interface.
 */
interface State : Parcelable, Output {
    fun toStateOutcome(showProgress: Boolean = true): Outcome = StateOutcome(this, showProgress)
    fun toStateOutcomeFlow(showProgress: Boolean = true): Flow<Outcome> = flowOf(toStateOutcome(showProgress))
    fun toStateOutcomeParallelFlow(showProgress: Boolean = true): Flow<Outcome> =
        toStateOutcomeFlow(showProgress).executeInParallel()
}
