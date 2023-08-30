package com.zeyadgasser.core.api

import android.os.Parcelable
import com.zeyadgasser.core.FluxViewModel.StateOutcome
import com.zeyadgasser.core.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * All states presented to the view and produced/reduced by the VM must implement this [State] interface.
 */
interface State : Parcelable, Output {
    fun toStateOutcome(): Outcome = StateOutcome(this)
    fun toStateOutcomeFlow(): Flow<Outcome> = flowOf(toStateOutcome())
    fun toStateOutcomeParallelFlow(): Flow<Outcome> = toStateOutcomeFlow().executeInParallel()
}
