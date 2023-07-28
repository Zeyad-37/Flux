package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.Outcome
import com.zeyadgasser.core.Outcome.EmptyOutcome.emptyOutcomeFlow
import com.zeyadgasser.core.api.executeInParallel
import com.zeyadgasser.core.api.toErrorOutcomeFlow
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MVIViewModel @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    initialState: MVIState,
    reducer: MVIReducer,
    handle: SavedStateHandle?,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FluxViewModel<MVIInput, MVIResult, MVIState, MVIEffect>(initialState, handle, reducer, dispatcher) {

    override fun handleInputs(input: MVIInput, state: MVIState): Flow<Outcome> =
        when (input) {
            ChangeBackgroundInput -> ChangeBackgroundResult(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
            ).toResultOutcomeFlow()
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorResult("Error").toResultOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input, state.color)
            is RemoveTask -> onRemoveTask(input.id, state.color)
            DoNothing -> emptyOutcomeFlow()
        }

    private fun onRemoveTask(id: Long, color: Long): Flow<Outcome> =
        ChangeBackgroundResult(color, fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }).toResultOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked, color: Long): Flow<Outcome> =
        ChangeBackgroundResult(
            color, fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
        ).toResultOutcomeFlow()
}
