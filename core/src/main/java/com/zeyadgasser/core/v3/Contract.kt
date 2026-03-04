package com.zeyadgasser.core.v3

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlin.reflect.KClass

/**
 * An [Input] represents an action that the view model can react to
 */
interface Input : Track {
    override val eventData: Map<String, String>
        get() = emptyMap()
}

data class CancelInput<I : Input>(val clazz: KClass<I>) : Input {
    override val eventData: Map<String, String> = emptyMap()
    override val eventName: String = "Cancel ${clazz.simpleName}"
}

interface Result

/**
 * An [Effect] is a special type of [Result] that represents a one-off action (e.g. navigation)
 *
 * [Effect]'s are presented to the view immediately rather than being reduced
 */
interface Effect : Result, Track

/**
 * A [State] represents the state of a screen
 */
interface State : Result, Track

/**
 * Wrapper class to attribute inputs and [Result]s as a pair.
 */
internal data class InputResultFlow(val input: Input, val results: Flow<Result>)

/**
 * A wrapper class to identify async flows vs sync flows
 */
class AsyncResultFlow(val flow: Flow<Result>) : Flow<Result> {
    override suspend fun collect(collector: FlowCollector<Result>) = Unit
}

interface InputHandler<I : Input, S : State> {
    fun clazz(): KClass<I>

    @Suppress("UNCHECKED_CAST")
    operator fun invoke(input: Input, state: State): Flow<Result> =
        handle(input = input as I, state = state as S)

    fun handle(input: I, state: S): Flow<Result>
}

typealias InputHandlerType = InputHandler<out Input, out State>
