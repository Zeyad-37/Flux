package com.zeyadgasser.mvvm

import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.api.Effect
import com.zeyadgasser.core.api.Input
import com.zeyadgasser.core.api.State
import com.zeyadgasser.domainPure.RED_LIGHT
import com.zeyadgasser.domainPure.WHITE
import kotlinx.parcelize.Parcelize

sealed class MVVMInput : Input()
object ChangeBackgroundInput : MVVMInput()
object ShowDialogInput : MVVMInput()
object ErrorInput : MVVMInput()
object UncaughtErrorInput : MVVMInput()
object NavBackInput : MVVMInput()
object DoNothing : MVVMInput()
data class RemoveTask(val id: Long) : MVVMInput()
data class ChangeTaskChecked(val id: Long, val checked: Boolean) : MVVMInput()
object CancelChangeBackgroundInput : MVVMInput()

sealed class MVVMEffect : Effect
object NavBackEffect : MVVMEffect()
object ShowDialogEffect : MVVMEffect()

sealed class MVVMState(open val color: Long = WHITE) : State

@Parcelize
object InitialState : MVVMState()

@Parcelize
data class ColorBackgroundState(
    override val color: Long,
    val list: List<FluxTaskItem>,
) : MVVMState(color)

@Parcelize
data class ErrorState(val message: String) : MVVMState(RED_LIGHT)
