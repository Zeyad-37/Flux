package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.v1.FluxViewModel
import com.zeyadgasser.core.v1.Outcome
import com.zeyadgasser.core.v1.api.emptyOutcomeFlow
import com.zeyadgasser.core.v1.api.executeInParallel
import com.zeyadgasser.core.v1.api.toErrorOutcomeFlow
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MVIViewModel @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    initialState: MVIState,
    reducer: MVIReducer,
    handle: SavedStateHandle?,
    dispatcher: CoroutineDispatcher = IO,
) : FluxViewModel<MVIInput, MVIResult, MVIState, MVIEffect>(initialState, null, handle, reducer, dispatcher) {

    override fun handleInputs(input: MVIInput, state: MVIState): Flow<Outcome> =
        when (input) {
            ChangeBackgroundInput -> onChangeBackground()
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow(null, input, true)
                .executeInParallel()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow().executeInParallel()
            ErrorInput -> ErrorResult("Error").toResultOutcomeFlow().executeInParallel()
            is ChangeTaskChecked -> onChangeTaskChecked(input, state.color).executeInParallel()
            is RemoveTask -> onRemoveTask(input.id, state.color).executeInParallel()
            DoNothing -> emptyOutcomeFlow(true)
        }

    private fun onChangeBackground(): Flow<Outcome> =
        ChangeBackgroundResult(
            getRandomColorIdUseCase.getRandomColorId(), fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
        ).toResultOutcomeFlow().onEach { delay(1000) }.makeCancellable(ChangeBackgroundInput::class)

    private fun onRemoveTask(id: Long, color: Long): Flow<Outcome> =
        ChangeBackgroundResult(
            color, fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }
        ).toResultOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked, color: Long): Flow<Outcome> =
        ChangeBackgroundResult(
            color, fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
        ).toResultOutcomeFlow(false)
}
