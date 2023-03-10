package com.zeyadgasser.mvi

import com.zeyadgasser.core.Reducer

class MVIReducer : Reducer<MVIState, MVIResult> {
    override fun reduce(state: MVIState, result: MVIResult): MVIState = when (result) {
        is ChangeBackgroundResult -> ColorBackgroundState(result.color, result.list)
        is ErrorResult -> ErrorState(result.message)
    }
}
