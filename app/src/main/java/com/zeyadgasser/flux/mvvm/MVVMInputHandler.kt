package com.zeyadgasser.flux.mvvm

import com.zeyadgasser.core.*
import com.zeyadgasser.flux.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlin.random.Random

class MVVMInputHandler : InputHandler<MVVMInput, MVVMState> {
    override fun handleInputs(input: MVVMInput, currentState: MVVMState): Flow<FluxOutcome> {
        return when (input) {
            ChangeBackgroundInput -> ColorBackgroundState(getRandomColorId())
                    .toStateOutcomeFlow().onStart { delay(1000) }
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorState("Error").toStateOutcomeFlow()
        }
    }

    private fun getRandomColorId(): Int = when (Random.nextInt(10)) {
        0 -> R.color.purple_200
        1 -> android.R.color.holo_orange_dark
        2 -> R.color.teal_200
        3 -> R.color.teal_700
        4 -> androidx.appcompat.R.color.material_blue_grey_800
        5 -> android.R.color.holo_green_light
        6 -> android.R.color.darker_gray
        7 -> android.R.color.holo_red_light
        8 -> android.R.color.holo_blue_dark
        9 -> android.R.color.holo_green_dark
        else -> android.R.color.black
    }
}
