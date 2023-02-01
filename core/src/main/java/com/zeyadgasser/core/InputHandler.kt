package com.zeyadgasser.core

import kotlinx.coroutines.flow.Flow

interface InputHandler<I : Input, S : State> {
    fun handleInputs(input: I, currentState: S): Flow<FluxOutcome>
}
