package com.zeyadgasser.mvvm

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.EmptyFluxOutcome.emptyOutcomeFlow
import com.zeyadgasser.core.FluxOutcome
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.executeInParallel
import com.zeyadgasser.core.toErrorOutcomeFlow
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MVVMViewModel @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    initialState: MVVMState,
    handle: SavedStateHandle?,
) : FluxViewModel<MVVMInput, Nothing, MVVMState, MVVMEffect>(initialState, handle) {

    override fun handleInputs(input: MVVMInput, currentState: MVVMState): Flow<FluxOutcome> =
        when (input) {
            ChangeBackgroundInput -> ColorBackgroundState(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
            ).toStateOutcomeFlow()
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorState("Error").toStateOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input, currentState.color)
            is RemoveTask -> onRemoveTask(input.id, currentState.color)
            DoNothing -> emptyOutcomeFlow()
        }

    private fun onRemoveTask(id: Long, color: Long): Flow<FluxOutcome> = ColorBackgroundState(
        color, fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }
    ).toStateOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked, color: Long): Flow<FluxOutcome> =
        ColorBackgroundState(
            color,
            fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
        ).toStateOutcomeFlow()
}
