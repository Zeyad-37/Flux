package com.zeyadgasser.mvi

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
class MVIViewModel @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    initialState: MVIState,
    reducer: MVIReducer,
    handle: SavedStateHandle?,
) : FluxViewModel<MVIInput, MVIResult, MVIState, MVIEffect>(initialState, handle, reducer) {

    override fun handleInputs(input: MVIInput, currentState: MVIState): Flow<FluxOutcome> =
        when (input) {
            ChangeBackgroundInput -> ChangeBackgroundResult(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
            ).toResultOutcomeFlow()
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorResult("Error").toResultOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input, currentState.color)
            is RemoveTask -> onRemoveTask(input.id, currentState.color)
            DoNothing -> emptyOutcomeFlow()
        }

    private fun onRemoveTask(id: Long, color: Long): Flow<FluxOutcome> = ChangeBackgroundResult(
        color, fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }
    ).toResultOutcomeFlow()

    private fun onChangeTaskChecked(
        input: ChangeTaskChecked, color: Long,
    ): Flow<FluxOutcome> = ChangeBackgroundResult(
        color,
        fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
    ).toResultOutcomeFlow()
}
