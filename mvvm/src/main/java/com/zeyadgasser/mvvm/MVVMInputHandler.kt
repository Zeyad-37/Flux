package com.zeyadgasser.mvvm

import com.zeyadgasser.composables.mappers.FluxTaskItemMapper
import com.zeyadgasser.core.FluxOutcome
import com.zeyadgasser.core.InputHandler
import com.zeyadgasser.core.toEffectOutcomeFlow
import com.zeyadgasser.core.toStateOutcomeFlow
import com.zeyadgasser.core.toErrorOutcomeFlow
import com.zeyadgasser.core.executeInParallel
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

private const val DELAY = 1000L

class MVVMInputHandler @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    private val fluxTaskItemMapper: FluxTaskItemMapper,
) : InputHandler<MVVMInput, MVVMState> {

    override fun handleInputs(input: MVVMInput, currentState: MVVMState): Flow<FluxOutcome> =
        when (input) {
            ChangeBackgroundInput -> ColorBackgroundState(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { fluxTaskItemMapper.map(it) }
            ).toStateOutcomeFlow().onStart { delay(DELAY) }
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorState("Error").toStateOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input)
            is RemoveTask -> onRemoveTask(input.id)
        }

    private fun onRemoveTask(id: Long): Flow<FluxOutcome> = ColorBackgroundState(
        getRandomColorIdUseCase.getRandomColorId(),
        fluxTaskUseCases.removeTask(id).map { fluxTaskItemMapper.map(it) }
    ).toStateOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked): Flow<FluxOutcome> =
        ColorBackgroundState(
            getRandomColorIdUseCase.getRandomColorId(),
            fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked)
                .map { fluxTaskItemMapper.map(it) }
        ).toStateOutcomeFlow()
}
