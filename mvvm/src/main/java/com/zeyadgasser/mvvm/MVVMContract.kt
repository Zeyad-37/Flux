package com.zeyadgasser.mvvm

import com.zeyadgasser.composables.presentation_models.FluxTaskItem
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.State
import com.zeyadgasser.domainPure.RedLight
import com.zeyadgasser.domainPure.White
import kotlinx.parcelize.Parcelize

sealed class MVVMInput : Input()
object ChangeBackgroundInput : MVVMInput()
object ShowDialogInput : MVVMInput()
object ErrorInput : MVVMInput()
object UncaughtErrorInput : MVVMInput()
object NavBackInput : MVVMInput()
data class RemoveTask(val id: Long) : MVVMInput()
data class ChangeTaskChecked(val id: Long, val checked: Boolean) : MVVMInput()

sealed class MVVMEffect : Effect
object NavBackEffect : MVVMEffect()
object ShowDialogEffect : MVVMEffect()

sealed class MVVMState(open val color: Long = White) : State

@Parcelize
object InitialState : MVVMState()

@Parcelize
data class ColorBackgroundState(
    override val color: Long,
    val list: List<FluxTaskItem>,
) : MVVMState(color)

@Parcelize
data class ErrorState(val message: String) : MVVMState(RedLight)
