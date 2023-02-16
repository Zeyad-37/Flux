package com.zeyadgasser.flux.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zeyadgasser.core.ARG_STATE
import com.zeyadgasser.core.FluxViewModel

class MVVMViewModel(
    initialState: MVVMState,
    inputHandler: MVVMInputHandler,
    handle: SavedStateHandle?
) : FluxViewModel<MVVMInput, Nothing, MVVMState, MVVMEffect>(
    initialState, inputHandler, null, handle
) {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                createSavedStateHandle().let {
                    MVVMViewModel(it[ARG_STATE] ?: InitialState, MVVMInputHandler(), it)
                }
            }
        }
    }
}
