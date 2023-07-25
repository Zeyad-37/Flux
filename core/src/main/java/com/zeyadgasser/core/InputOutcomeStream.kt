package com.zeyadgasser.core

import com.zeyadgasser.core.api.Input
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper class to attribute inputs and [Outcome]s as a pair.
 */
internal data class InputOutcomeStream(val input: Input, val outcomes: Flow<Outcome>)
