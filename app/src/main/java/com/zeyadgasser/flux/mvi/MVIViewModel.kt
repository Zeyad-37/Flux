package com.zeyadgasser.flux.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zeyadgasser.core.FluxViewModel

class MVIViewModel(inputHandler: MVIInputHandler, reducer: MVIReducer, handle: SavedStateHandle?) :
    FluxViewModel<MVIInput, MVIResult, MVIState, MVIEffect>(inputHandler, reducer, handle) {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { MVIViewModel(MVIInputHandler(), MVIReducer(), createSavedStateHandle()) }
        }
    }
}
