package com.zeyadgasser.flux.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zeyadgasser.core.ARG_STATE
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.InputStrategy.THROTTLE
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MVIViewModel @Inject constructor(
    initialState: MVIState,
    inputHandler: MVIInputHandler,
    reducer: MVIReducer,
    handle: SavedStateHandle?,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FluxViewModel<MVIInput, MVIResult, MVIState, MVIEffect>(
    initialState, inputHandler, reducer, handle, dispatcher
) {

    fun changeBackground() = process(ChangeBackgroundInput, THROTTLE)
    fun showDialogInput() = process(ShowDialogInput)
    fun errorInput() = process(ErrorInput)
    fun uncaughtErrorInput() = process(UncaughtErrorInput)
    fun navBackInput() = process(NavBackInput)

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
