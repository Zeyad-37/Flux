package com.zeyadgasser.flux.mvi

import com.zeyadgasser.core.*
import com.zeyadgasser.flux.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlin.random.Random

class MVIInputHandler : InputHandler<MVIInput, MVIState> {
    override fun handleInputs(input: MVIInput, currentState: MVIState): Flow<FluxOutcome> =
        when (input) {
            is ChangeBackgroundInput -> ChangeBackgroundResult(getRandomColorId())
                .toResultOutcomeFlow().onStart { delay(1373) }
            is ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            is ErrorInput -> IllegalStateException("Error").toErrorOutcomeFlow()
        }

    private fun getRandomColorId(): Int = when (Random.nextInt(10)) {
        0 -> R.color.purple_200
        1 -> android.R.color.holo_orange_dark
        2 -> R.color.teal_200
        3 -> R.color.teal_700
        4 -> R.color.white
        5 -> R.color.black
        6 -> android.R.color.darker_gray
        7 -> android.R.color.holo_red_light
        8 -> android.R.color.holo_blue_dark
        9 -> android.R.color.holo_green_dark
        else -> android.R.color.holo_purple
    }
}
