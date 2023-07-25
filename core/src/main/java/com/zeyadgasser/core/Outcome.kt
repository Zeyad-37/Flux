package com.zeyadgasser.core

import com.zeyadgasser.core.api.EmptyInput
import com.zeyadgasser.core.api.Error
import com.zeyadgasser.core.api.Input
import com.zeyadgasser.core.api.Progress
import kotlinx.coroutines.flow.flowOf

/**
 * [Outcome]s are the return type of the handleInputs function in the ViewModel. An internal data structure
 * for to manage the UDF pattern with input attribution for emissions.
 */
sealed class Outcome(open var input: Input = EmptyInput) : Loggable {
    /**
     * An [Outcome] to be ignored
     */
    object EmptyOutcome : Outcome() {
        fun emptyOutcomeFlow() = flowOf(EmptyOutcome)
    }

    /**
     * Internal wrapper class to manage [Progress] emissions
     */
    internal data class ProgressOutcome(val progress: Progress) : Outcome() {
        constructor(isLoading: Boolean, input: Input) : this(Progress(isLoading, input))
    }

    /**
     * Internal wrapper class to manage [Error] emissions
     */
    data class ErrorOutcome(val error: Error, override var input: Input = EmptyInput) : Outcome(input) {
        constructor(cause: Throwable, errorMessage: String? = null, input: Input = EmptyInput) :
                this(Error(errorMessage ?: cause.message.orEmpty(), cause, input), input)
    }
}
