package com.zeyadgasser.flux.mvvm

import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.State
import kotlinx.parcelize.Parcelize

sealed class MVVMInput : Input()
object ChangeBackgroundInput : MVVMInput()
object ShowDialogInput : MVVMInput()
object ErrorInput : MVVMInput()
object UncaughtErrorInput : MVVMInput()
object NavBackInput : MVVMInput()

sealed class MVVMEffect : Effect
object NavBackEffect : MVVMEffect()
object ShowDialogEffect : MVVMEffect()

sealed class MVVMState(open val color: Int = android.R.color.white) : State

@Parcelize
object InitialState : MVVMState()

@Parcelize
data class ColorBackgroundState(override val color: Int) : MVVMState(color)

@Parcelize
data class ErrorState(val message: String) : MVVMState(android.R.color.holo_red_light)
