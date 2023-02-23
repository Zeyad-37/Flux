package com.zeyadgasser.mvi

import com.zeyadgasser.composables.presentation_models.FluxTaskItem
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.Result
import com.zeyadgasser.core.State
import kotlinx.parcelize.Parcelize

sealed class MVIInput : Input()
object ChangeBackgroundInput : MVIInput()
object ShowDialogInput : MVIInput()
object UncaughtErrorInput : MVIInput()
object ErrorInput : MVIInput()
object NavBackInput : MVIInput()
data class RemoveTask(val id: Long) : MVIInput()
data class ChangeTaskChecked(val id: Long, val checked: Boolean) : MVIInput()

sealed class MVIResult : Result
data class ChangeBackgroundResult(val color: Long, val list: List<FluxTaskItem>) : MVIResult()
data class ErrorResult(val message: String) : MVIResult()

sealed class MVIEffect : Effect
object ShowDialogEffect : MVIEffect()
object NavBackEffect : MVIEffect()

sealed class MVIState(open val color: Long = 0xffffffff /*white*/) : State

@Parcelize
object InitialState : MVIState()

@Parcelize
data class ErrorState(val message: String) : MVIState(0xffff4444 /*holo_red_light*/)

@Parcelize
data class ColorBackgroundState(override val color: Long, val list: List<FluxTaskItem>) :
    MVIState(color)
