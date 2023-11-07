package com.zeyadgasser.core

import com.zeyadgasser.core.api.Input
import com.zeyadgasser.core.api.Result
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper class to attribute inputs and [Result]s as a pair. A key value pair basically.
 */
internal data class InputOutcomeStream(val input: Input, val resultFlow: Flow<Result>)
