package com.zeyadgasser.mvi

import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.Debounce
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.InputStrategy
import com.zeyadgasser.core.NONE
import com.zeyadgasser.core.Result
import com.zeyadgasser.core.State
import com.zeyadgasser.core.Throttle
import com.zeyadgasser.domainPure.RED_LIGHT
import com.zeyadgasser.domainPure.WHITE
import kotlinx.parcelize.Parcelize

sealed class MVIInput(
    showProgress: Boolean = true, inputStrategy: InputStrategy = NONE
) : Input(showProgress, inputStrategy)

object ChangeBackgroundInput : MVIInput(inputStrategy = Debounce())
object ShowDialogInput : MVIInput()
object UncaughtErrorInput : MVIInput(inputStrategy = Throttle(300L))
object ErrorInput : MVIInput()
object NavBackInput : MVIInput()
object DoNothing : MVIInput(inputStrategy = Throttle(500L))
data class RemoveTask(val id: Long) : MVIInput()
data class ChangeTaskChecked(val id: Long, val checked: Boolean) : MVIInput(false)

sealed class MVIResult : Result
data class ChangeBackgroundResult(val color: Long, val list: List<FluxTaskItem>) : MVIResult()
data class ErrorResult(val message: String) : MVIResult()

sealed class MVIEffect : Effect
object ShowDialogEffect : MVIEffect()
object NavBackEffect : MVIEffect()

sealed class MVIState(open val color: Long = WHITE) : State

@Parcelize
object InitialState : MVIState()

@Parcelize
data class ErrorState(val message: String) : MVIState(RED_LIGHT)

@Parcelize
data class ColorBackgroundState(
    override val color: Long, val list: List<FluxTaskItem>
) : MVIState(color)
