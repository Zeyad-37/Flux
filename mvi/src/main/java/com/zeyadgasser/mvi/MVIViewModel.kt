package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.FluxOutcome
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.InputStrategy.THROTTLE
import com.zeyadgasser.core.emptyOutcomeFlow
import com.zeyadgasser.core.executeInParallel
import com.zeyadgasser.core.toEffectOutcomeFlow
import com.zeyadgasser.core.toErrorOutcomeFlow
import com.zeyadgasser.core.toResultOutcomeFlow
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

private const val DELAY = 1373L

@HiltViewModel
class MVIViewModel @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    initialState: MVIState,
    reducer: MVIReducer,
    handle: SavedStateHandle?,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FluxViewModel<MVIInput, MVIResult, MVIState, MVIEffect>(
    initialState, reducer, handle, dispatcher
) {
    fun changeBackground() = process(ChangeBackgroundInput, THROTTLE)
    fun showDialogInput() = process(ShowDialogInput)
    fun errorInput() = process(ErrorInput)
    fun uncaughtErrorInput() = process(UncaughtErrorInput)
    fun navBackInput() = process(NavBackInput)
    fun removeTask(id: Long) = process(RemoveTask(id))
    fun changeTaskChecked(id: Long, checked: Boolean) = process(ChangeTaskChecked(id, checked))
    fun doNothing() = process(DoNothing)

    override fun handleInputs(input: MVIInput, currentState: MVIState): Flow<FluxOutcome> =
        when (input) {
            ChangeBackgroundInput -> ChangeBackgroundResult(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
            ).toResultOutcomeFlow().onStart { delay(DELAY) }
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorResult("Error").toResultOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input, currentState)
            is RemoveTask -> onRemoveTask(input.id)
            DoNothing -> emptyOutcomeFlow()
        }

    private fun onRemoveTask(id: Long): Flow<FluxOutcome> = ChangeBackgroundResult(
        getRandomColorIdUseCase.getRandomColorId(),
        fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }
    ).toResultOutcomeFlow()

    private fun onChangeTaskChecked(
        input: ChangeTaskChecked, currentState: MVIState
    ): Flow<FluxOutcome> = ChangeBackgroundResult(
        currentState.color,
        fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
    ).toResultOutcomeFlow()
}
