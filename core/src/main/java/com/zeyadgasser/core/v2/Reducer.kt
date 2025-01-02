package com.zeyadgasser.core.v2

/**
 * A [Reducer] is a pure function which takes the current [State] and a [Result] and returns a new [State]
 */
interface Reducer<R : Result, S : State> {

    /**
     * @param result the [Result] to be handled
     * @param state the current [State]
     *
     * @return the new [State]
     */
    fun reduce(result: R, state: S): S
}
