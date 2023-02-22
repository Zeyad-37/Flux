package com.zeyadgasser.mvi

import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.Result
import com.zeyadgasser.core.State
import com.zeyadgasser.domain.FluxTask
import kotlinx.parcelize.Parcelize

sealed class MVIInput : Input()
object ChangeBackgroundInput : MVIInput()
object ShowDialogInput : MVIInput()
object UncaughtErrorInput : MVIInput()
object ErrorInput : MVIInput()
object NavBackInput : MVIInput()
data class RemoveTask(val fluxTask: FluxTask) : MVIInput()
data class ChangeTaskChecked(val fluxTask: FluxTask, val checked: Boolean) : MVIInput()

sealed class MVIResult : Result
data class ChangeBackgroundResult(val color: Int, val list: List<FluxTask>) : MVIResult()
data class ErrorResult(val message: String) : MVIResult()

sealed class MVIEffect : Effect
object ShowDialogEffect : MVIEffect()
object NavBackEffect : MVIEffect()

sealed class MVIState(open val color: Int = android.R.color.white) : State

@Parcelize
data class ErrorState(val message: String) : MVIState(android.R.color.holo_red_light)

@Parcelize
data class ColorBackgroundState(override val color: Int, val list: List<FluxTask>) : MVIState(color)

@Parcelize
object InitialState : MVIState()
