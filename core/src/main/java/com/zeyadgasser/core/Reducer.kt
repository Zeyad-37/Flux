package com.zeyadgasser.core

/**
 * To implement the MVI pattern, a class is created that implements the [Reducer] interface, to indicate the
 * transition rules between the [State]s
 */
interface Reducer<S : State, R : Result> {
    /**
     * Given a [State] and a [Result], return a new [State]
     */
    fun reduce(state: S, result: R): S
}
