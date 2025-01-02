package com.zeyadgasser.core.v1

import com.zeyadgasser.core.v1.api.Input
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper class to attribute inputs and [Outcome]s as a pair.
 */
internal data class InputOutcomeStream(val input: Input, val outcomes: Flow<Outcome>)
