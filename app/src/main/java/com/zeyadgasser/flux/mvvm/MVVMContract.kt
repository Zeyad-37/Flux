package com.zeyadgasser.flux.mvvm

import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.Result
import com.zeyadgasser.core.State
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

sealed class MVVMInput : Input()
data class ChangeBackgroundInput(
    val r: Int = Random.nextInt(255),
    val g: Int = Random.nextInt(255),
    val b: Int = Random.nextInt(255)
) : MVVMInput()

object ShowDialogInput : MVVMInput()
object ErrorInput : MVVMInput()

sealed class MVVMResult : Result
object ChangeBackgroundResult : MVVMResult()

sealed class MVVMEffect : Effect

object ShowDialogEffect : MVVMEffect()

sealed class MVVMState : State

@Parcelize
data class ColorBackgroundState(val color: Int) : MVVMState()

@Parcelize
object InitialState : MVVMState()
