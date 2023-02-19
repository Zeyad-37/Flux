package com.zeyadgasser.flux.mvi

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.InputStrategy.THROTTLE
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
    fun removeTask(task: FluxTask) = process(RemoveTask(task))
    fun changeTaskChecked(task: FluxTask, checked: Boolean) = process(ChangeTaskChecked(task, checked))
}
