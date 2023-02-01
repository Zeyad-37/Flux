package com.zeyadgasser.core

interface Reducer<S : State, R : Result> {
    fun reduce(state: S, result: R): S
}
