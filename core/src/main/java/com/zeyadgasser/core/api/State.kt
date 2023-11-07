package com.zeyadgasser.core.api

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * All states presented to the view and produced/reduced by the VM must implement this [State] interface.
 */
interface State : Parcelable, Result, Output

fun State.toStateResultFlow(): Flow<Result> = flowOf(this)
fun State.toStateResultParallelFlow(): Flow<Result> = toStateResultFlow().inParallel()
