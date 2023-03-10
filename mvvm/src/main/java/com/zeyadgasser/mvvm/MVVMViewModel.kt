package com.zeyadgasser.mvvm

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.FluxOutcome
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.InputStrategy.THROTTLE
import com.zeyadgasser.core.emptyOutcomeFlow
import com.zeyadgasser.core.executeInParallel
import com.zeyadgasser.core.toEffectOutcomeFlow
import com.zeyadgasser.core.toErrorOutcomeFlow
import com.zeyadgasser.core.toStateOutcomeFlow
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

private const val DELAY = 1000L

@HiltViewModel
class MVVMViewModel @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    initialState: MVVMState,
    handle: SavedStateHandle?,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FluxViewModel<MVVMInput, Nothing, MVVMState, MVVMEffect>(
    initialState, null, handle, dispatcher
) {
    fun changeBackground() = process(ChangeBackgroundInput, THROTTLE)
    fun showDialogInput() = process(ShowDialogInput)
    fun errorInput() = process(ErrorInput)
    fun uncaughtErrorInput() = process(UncaughtErrorInput)
    fun navBackInput() = process(NavBackInput)
    fun removeTask(id: Long) = process(RemoveTask(id))
    fun changeTaskChecked(id: Long, checked: Boolean) = process(ChangeTaskChecked(id, checked))
    fun doNothing() = process(DoNothing)

    override fun handleInputs(input: MVVMInput, currentState: MVVMState): Flow<FluxOutcome> =
        when (input) {
            ChangeBackgroundInput -> ColorBackgroundState(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
            ).toStateOutcomeFlow().onStart { delay(DELAY) }
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorState("Error").toStateOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input)
            is RemoveTask -> onRemoveTask(input.id)
            DoNothing -> emptyOutcomeFlow()
        }

    private fun onRemoveTask(id: Long): Flow<FluxOutcome> = ColorBackgroundState(
        getRandomColorIdUseCase.getRandomColorId(),
        fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }
    ).toStateOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked): Flow<FluxOutcome> =
        ColorBackgroundState(
            getRandomColorIdUseCase.getRandomColorId(),
            fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
        ).toStateOutcomeFlow()
}
