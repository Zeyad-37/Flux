package com.zeyadgasser.flux.mvi

import com.zeyadgasser.core.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class MVIInputHandler : InputHandler<MVIInput, MVIState> {
    override fun handleInputs(input: MVIInput, currentState: MVIState): Flow<FluxOutcome> =
        when (input) {
            is ChangeBackgroundInput ->
                ChangeBackgroundResult.toResultOutcomeFlow().onStart { delay(1373) }
            is ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            is ErrorInput -> IllegalStateException("Error").toErrorOutcomeFlow()
        }
}
