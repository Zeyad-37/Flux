package com.zeyadgasser.core.api

import com.zeyadgasser.core.Loggable
import kotlin.reflect.KClass

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

/**
 * All Inputs defined must extend this class.
 *  @param inputStrategy used to decide whether to debounce, delay or do nothing to the input.
 */
open class Input(open val inputStrategy: InputStrategy = NONE) : Loggable {
    override fun toString() =
        "${this::class.simpleName}(inputStrategy=${inputStrategy}ms)"
}

/**
 * All emissions have to be matched with a input, except the initial state that is not matched to an input,
 * so we use this EmptyInput.
 */
data object EmptyInput : Input()

data class CancelInput<I : Input>(
    val clazz: KClass<I>, val showProgress: Boolean, override val inputStrategy: InputStrategy = NONE
) : Input(inputStrategy)
