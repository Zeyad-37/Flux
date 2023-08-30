package com.zeyadgasser.mvvm

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
class MVVMViewModel @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    initialState: MVVMState,
    handle: SavedStateHandle?,
    dispatcher: CoroutineDispatcher = IO,
) : FluxViewModel<MVVMInput, Nothing, MVVMState, MVVMEffect>(
    initialState, savedStateHandle = handle, dispatcher = dispatcher
) {

    private val cancellationFlag: AtomicBoolean = AtomicBoolean(false)

    override fun handleInputs(input: MVVMInput, state: MVVMState): Flow<Outcome> =
        when (input) {
            ChangeBackgroundInput -> onChangeBackground()
            CancelChangeBackgroundInput -> onCancelChangeBackground()
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorState("Error").toStateOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input, state.color)
            is RemoveTask -> onRemoveTask(input.id, state.color)
            DoNothing -> emptyOutcomeFlow()
        }

    private fun onChangeBackground(): Flow<Outcome> =
        ColorBackgroundState(
            getRandomColorIdUseCase.getRandomColorId(),
            fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
        ).toStateOutcomeFlow().onEach {
            cancellationFlag.set(false)
            delay(1000)
        }.takeWhile { !cancellationFlag.get() }

    private fun onCancelChangeBackground(): Flow<EmptyOutcome> = emptyOutcomeFlow().also { cancellationFlag.set(true) }

    private fun onRemoveTask(id: Long, color: Long): Flow<Outcome> =
        ColorBackgroundState(color, fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }).toStateOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked, color: Long): Flow<Outcome> =
        ColorBackgroundState(
            color, fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
        ).toStateOutcomeFlow()
}
