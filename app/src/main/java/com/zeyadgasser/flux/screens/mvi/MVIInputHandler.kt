package com.zeyadgasser.flux.screens.mvi

import com.zeyadgasser.core.*
import com.zeyadgasser.flux.screens.FluxTaskUseCases
import com.zeyadgasser.flux.screens.GetRandomColorIdUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MVIInputHandler @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
) :
    InputHandler<MVIInput, MVIState> {
    private val tasks = List(30) { i -> FluxTask(i, "Task # $i") }.toMutableList()

    override fun handleInputs(input: MVIInput, currentState: MVIState): Flow<FluxOutcome> =
        when (input) {
            ChangeBackgroundInput -> ChangeBackgroundResult(
                getRandomColorIdUseCase.getRandomColorId(), tasks
            ).toResultOutcomeFlow().onStart { delay(1373) }
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorResult("Error").toResultOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input)
            is RemoveTask -> onRemoveTask(input.fluxTask)
        }

    private fun onRemoveTask(task: FluxTask): Flow<FluxOutcome> =
        takeIf { fluxTaskUseCases.removeTask(tasks, task) }
            ?.run { ChangeBackgroundResult(getRandomColorIdUseCase.getRandomColorId(), tasks) }
            ?.toResultOutcomeFlow()
            ?: Exception("Couldn't remove task from list").toErrorOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked): Flow<FluxOutcome> =
        fluxTaskUseCases.onChangeTaskChecked(tasks, input.fluxTask, input.checked)
            .run { ChangeBackgroundResult(getRandomColorIdUseCase.getRandomColorId(), tasks) }
            .toResultOutcomeFlow()
}
