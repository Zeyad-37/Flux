package com.zeyadgasser.core

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
 *  @param getShowProgress used to decide whether to emit a [Progress].
 *  @param inputStrategy used to decide whether to debounce, delay or do nothing to the input.
 */
open class Input(
    open var getShowProgress: () -> Boolean = { true }, open val inputStrategy: InputStrategy = NONE,
) : Loggable {
    override fun toString(): String =
        "${this::class.simpleName}(showProgress=${getShowProgress()}, inputStrategy=${inputStrategy}ms)"
}

/**
 * All emissions have to be matched with a input, except the initial state that is not matched to an input,
 * so we use this EmptyInput.
 */
data object EmptyInput : Input()

data class CancelInput<I : Input>(
    val clazz: KClass<I>,
    override val inputStrategy: InputStrategy = NONE,
    override var getShowProgress: () -> Boolean = { true },
) : Input(getShowProgress, inputStrategy)
