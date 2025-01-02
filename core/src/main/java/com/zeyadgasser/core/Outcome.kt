package com.zeyadgasser.core

import com.zeyadgasser.core.api.Error
import com.zeyadgasser.core.api.Input
import com.zeyadgasser.core.api.Progress


/**
 * [Outcome]s are the return type of the handleInputs function in the ViewModel. An internal data structure
 * for to manage the UDF pattern with input attribution for emissions.
 */
sealed class Outcome(open val showProgress: Boolean) : Loggable {
    /**
     * An [Outcome] to be ignored
     */
    data class EmptyOutcome(override val showProgress: Boolean = false) : Outcome(showProgress)

    /**
     * Internal wrapper class to manage [Progress] emissions
     */
    internal data class ProgressOutcome(val progress: Progress) : Outcome(progress.isLoading) {
        constructor(isLoading: Boolean, input: Input) : this(Progress(isLoading, input))
    }

    /**
     * Internal wrapper class to manage [Error] emissions
     */
    data class ErrorOutcome(val error: Error, override var showProgress: Boolean = true) : Outcome(showProgress) {
        constructor(cause: Throwable, input: Input, errorMessage: String? = null, showProgress: Boolean = true) :
                this(Error(errorMessage ?: cause.message.orEmpty(), cause, input), showProgress)
    }
}
