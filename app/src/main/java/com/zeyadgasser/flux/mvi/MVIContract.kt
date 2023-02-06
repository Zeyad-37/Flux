package com.zeyadgasser.flux.mvi

import android.graphics.Color
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
object ErrorInput : MVIInput()

sealed class MVIResult : Result
data class ChangeBackgroundResult(val color: Int) : MVIResult()

sealed class MVIEffect : Effect

object ShowDialogEffect : MVIEffect()

sealed class MVIState : State

@Parcelize
data class ColorBackgroundState(val color: Int) : MVIState()

@Parcelize
object InitialState : MVIState()
