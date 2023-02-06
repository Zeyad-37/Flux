package com.zeyadgasser.flux.mvvm

import android.graphics.Color
import com.zeyadgasser.core.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class MVVMInputHandler : InputHandler<MVVMInput, MVVMState> {
    override fun handleInputs(input: MVVMInput, currentState: MVVMState): Flow<FluxOutcome> {
        return when (input) {
            is ChangeBackgroundInput ->
                ColorBackgroundState(Color.argb(255, input.r, input.g, input.b))
                    .toStateOutcomeFlow().onStart { delay(3000) }
            is ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            is ErrorInput -> IllegalStateException("Test").toErrorOutcomeFlow()
        }
    }
}
