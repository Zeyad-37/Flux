package com.zeyadgasser.flux.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zeyadgasser.core.ARG_STATE
import com.zeyadgasser.core.FluxViewModel

class MVIViewModel(
    initialState: MVIState,
    inputHandler: MVIInputHandler,
    reducer: MVIReducer,
    handle: SavedStateHandle?
) : FluxViewModel<MVIInput, MVIResult, MVIState, MVIEffect>(
    initialState, inputHandler, reducer, handle
) {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                createSavedStateHandle().let {
                    MVIViewModel(
                        it[ARG_STATE] ?: InitialState, MVIInputHandler(), MVIReducer(), it
                    )
                }
            }
        }
    }
}
