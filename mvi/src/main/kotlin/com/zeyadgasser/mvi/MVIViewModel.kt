package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.Outcome
import com.zeyadgasser.core.Outcome.EmptyOutcome
import com.zeyadgasser.core.Outcome.EmptyOutcome.emptyOutcomeFlow
import com.zeyadgasser.core.api.executeInParallel
import com.zeyadgasser.core.api.toErrorOutcomeFlow
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class MVIViewModel @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    initialState: MVIState,
    reducer: MVIReducer,
    handle: SavedStateHandle?,
    dispatcher: CoroutineDispatcher = IO,
) : FluxViewModel<MVIInput, MVIResult, MVIState, MVIEffect>(initialState, handle, reducer, dispatcher) {

    private val cancellationFlag: AtomicBoolean = AtomicBoolean(false)

    override fun handleInputs(input: MVIInput, state: MVIState): Flow<Outcome> =
        when (input) {
            ChangeBackgroundInput -> onChangeBackground()
            CancelChangeBackgroundInput -> onCancelChangeBackground()
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow().executeInParallel()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow().executeInParallel()
            ErrorInput -> ErrorResult("Error").toResultOutcomeFlow().executeInParallel()
            is ChangeTaskChecked -> onChangeTaskChecked(input, state.color).executeInParallel()
            is RemoveTask -> onRemoveTask(input.id, state.color).executeInParallel()
            DoNothing -> emptyOutcomeFlow()
        }

    private fun onChangeBackground(): Flow<Outcome> =
        ChangeBackgroundResult(
            getRandomColorIdUseCase.getRandomColorId(), fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
        ).toResultOutcomeFlow().onEach {
            cancellationFlag.set(false)
            delay(1000)
        }.takeWhile { !cancellationFlag.get() }

    private fun onCancelChangeBackground(): Flow<EmptyOutcome> = emptyOutcomeFlow().also { cancellationFlag.set(true) }

    private fun onRemoveTask(id: Long, color: Long): Flow<Outcome> =
        ChangeBackgroundResult(color, fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }).toResultOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked, color: Long): Flow<Outcome> =
        ChangeBackgroundResult(
            color, fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
        ).toResultOutcomeFlow()
}
