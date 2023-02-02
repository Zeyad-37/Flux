package com.zeyadgasser.flux.mvi

import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Input
import com.zeyadgasser.core.Result
import com.zeyadgasser.core.State
import kotlinx.parcelize.Parcelize

sealed class MVIInput : Input()
object ChangeBackgroundInput : MVIInput()
object ShowDialogInput : MVIInput()
object ErrorInput : MVIInput()

sealed class MVIResult : Result
object ChangeBackgroundResult : MVIResult()

sealed class MVIEffect : Effect

object ShowDialogEffect : MVIEffect()

sealed class MVIState : State

@Parcelize
object RedBackgroundState : MVIState()

@Parcelize
object InitialState : MVIState()
