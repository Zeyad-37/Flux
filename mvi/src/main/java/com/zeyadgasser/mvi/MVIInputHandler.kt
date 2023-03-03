package com.zeyadgasser.mvi

import com.zeyadgasser.composables.mappers.FluxTaskItemMapper
import com.zeyadgasser.core.FluxOutcome
import com.zeyadgasser.core.InputHandler
import com.zeyadgasser.core.emptyOutcomeFlow
import com.zeyadgasser.core.executeInParallel
import com.zeyadgasser.core.toEffectOutcomeFlow
import com.zeyadgasser.core.toErrorOutcomeFlow
import com.zeyadgasser.core.toResultOutcomeFlow
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

private const val DELAY = 1373L

class MVIInputHandler @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
    private val fluxTaskItemMapper: FluxTaskItemMapper,
) : InputHandler<MVIInput, MVIState> {

    override fun handleInputs(input: MVIInput, currentState: MVIState): Flow<FluxOutcome> =
        when (input) {
            ChangeBackgroundInput -> ChangeBackgroundResult(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { fluxTaskItemMapper.map(it) }
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
        fluxTaskUseCases.removeTask(id).map { fluxTaskItemMapper.map(it) }
    ).toResultOutcomeFlow()

    private fun onChangeTaskChecked(
        input: ChangeTaskChecked, currentState: MVIState
    ): Flow<FluxOutcome> = ChangeBackgroundResult(
        currentState.color,
        fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked)
            .map { fluxTaskItemMapper.map(it) }
    ).toResultOutcomeFlow()
}
