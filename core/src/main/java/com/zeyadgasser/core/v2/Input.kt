package com.zeyadgasser.core.v2

import kotlin.reflect.KClass

/**
 * An [Input] represents an action that the view model can react to
 */
interface Input {
    val inputStrategy: InputStrategy
}

private const val DEFAULT_INTERVAL = 370L

/**
 * Every [Input] has an [InputStrategy] as a parameter.
 */
sealed class InputStrategy(val interval: Long = DEFAULT_INTERVAL)

/**
 * Indicates that the input should be process normally.
 */
data object NONE : InputStrategy(0L)

/**
 * Indicates that the input should be throttled with the [DEFAULT_INTERVAL] or a provided [customInterval].
 */
data class Throttle(private val customInterval: Long = DEFAULT_INTERVAL) : InputStrategy(customInterval)

/**
 * Indicates that the input should be debounced with the [DEFAULT_INTERVAL] or a provided [customInterval].
 */
data class Debounce(private val customInterval: Long = DEFAULT_INTERVAL) : InputStrategy(customInterval)

data class CancelInput<I : Input>(
    val clazz: KClass<I>, override val inputStrategy: InputStrategy = NONE
) : Input
