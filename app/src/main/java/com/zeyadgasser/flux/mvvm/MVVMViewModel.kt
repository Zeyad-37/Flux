package com.zeyadgasser.flux.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zeyadgasser.core.FluxViewModel

class MVVMViewModel(inputHandler: MVVMInputHandler, handle: SavedStateHandle?) :
    FluxViewModel<MVVMInput, MVVMResult, MVVMState, MVVMEffect>(inputHandler, null, handle) {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { MVVMViewModel(MVVMInputHandler(), createSavedStateHandle()) }
        }
    }
}