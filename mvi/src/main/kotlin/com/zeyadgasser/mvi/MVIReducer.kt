package com.zeyadgasser.mvi

import com.zeyadgasser.core.Reducer

class MVIReducer : Reducer<MVIState, MVIResult> {
    override fun reduce(state: MVIState, result: MVIResult): MVIState = when (result) {
        is ChangeBackgroundResult -> when (state) {
            is ColorBackgroundState, is ErrorState, InitialState -> ColorBackgroundState(result.color, result.list)
        }

        is ErrorResult -> when (state) {
            is ColorBackgroundState, is ErrorState, InitialState -> ErrorState(result.message)
        }
    }
}
