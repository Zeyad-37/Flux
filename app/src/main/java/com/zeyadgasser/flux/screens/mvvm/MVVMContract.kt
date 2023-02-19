package com.zeyadgasser.flux.screens.mvvm

import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.State
import com.zeyadgasser.flux.screens.mvi.FluxTask
import kotlinx.parcelize.Parcelize

sealed class MVVMInput : Input()
object ChangeBackgroundInput : MVVMInput()
object ShowDialogInput : MVVMInput()
object ErrorInput : MVVMInput()
object UncaughtErrorInput : MVVMInput()
object NavBackInput : MVVMInput()
data class RemoveTask(val fluxTask: FluxTask) : MVVMInput()
data class ChangeTaskChecked(val fluxTask: FluxTask, val checked: Boolean) : MVVMInput()

sealed class MVVMEffect : Effect
object NavBackEffect : MVVMEffect()
object ShowDialogEffect : MVVMEffect()

sealed class MVVMState(open val color: Int = android.R.color.white) : State

@Parcelize
object InitialState : MVVMState()

@Parcelize
data class ColorBackgroundState(
    override val color: Int,
    val list: List<FluxTask>
) : MVVMState(color)

@Parcelize
data class ErrorState(val message: String) : MVVMState(android.R.color.holo_red_light)
