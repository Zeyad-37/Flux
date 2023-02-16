package com.zeyadgasser.flux.mvi

import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.Result
import com.zeyadgasser.core.State
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

sealed class MVIInput : Input()
data class ChangeBackgroundInput(
    val r: Int = Random.nextInt(255),
    val g: Int = Random.nextInt(255),
    val b: Int = Random.nextInt(255)
) : MVIInput()

object ShowDialogInput : MVIInput()
object UncaughtErrorInput : MVIInput()
object ErrorInput : MVIInput()
object NavBackInput : MVIInput()

sealed class MVIResult : Result
data class ChangeBackgroundResult(val color: Int) : MVIResult()
data class ErrorResult(val message: String) : MVIResult()

sealed class MVIEffect : Effect
object ShowDialogEffect : MVIEffect()
object NavBackEffect : MVIEffect()

sealed class MVIState(open val color: Int = android.R.color.white) : State

@Parcelize
data class ErrorState(val message: String) : MVIState(android.R.color.holo_red_light)

@Parcelize
data class ColorBackgroundState(override val color: Int) : MVIState(color)

@Parcelize
object InitialState : MVIState()
