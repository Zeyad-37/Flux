package com.zeyadgasser.mvvm

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.InputStrategy.THROTTLE
import com.zeyadgasser.domain.FluxTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MVVMViewModel @Inject constructor(
    initialState: MVVMState,
    inputHandler: MVVMInputHandler,
    handle: SavedStateHandle?,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FluxViewModel<MVVMInput, Nothing, MVVMState, MVVMEffect>(
    initialState, inputHandler, null, handle, dispatcher
) {
    fun changeBackground() = process(ChangeBackgroundInput, THROTTLE)
    fun showDialogInput() = process(ShowDialogInput)
    fun errorInput() = process(ErrorInput)
    fun uncaughtErrorInput() = process(UncaughtErrorInput)
    fun navBackInput() = process(NavBackInput)
    fun removeTask(task: FluxTask) = process(RemoveTask(task))
    fun changeTaskChecked(task: FluxTask, checked: Boolean) =
        process(ChangeTaskChecked(task, checked))
}
