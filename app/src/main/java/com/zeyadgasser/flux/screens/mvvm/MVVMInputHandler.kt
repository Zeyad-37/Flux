package com.zeyadgasser.flux.screens.mvvm

import com.zeyadgasser.core.*
import com.zeyadgasser.flux.screens.FluxTaskUseCases
import com.zeyadgasser.flux.screens.GetRandomColorIdUseCase
import com.zeyadgasser.flux.screens.mvi.ChangeBackgroundResult
import com.zeyadgasser.flux.screens.mvi.FluxTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MVVMInputHandler @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases
) : InputHandler<MVVMInput, MVVMState> {

    private val tasks = List(30) { i -> FluxTask(i, "Task # $i") }.toMutableList()

    override fun handleInputs(input: MVVMInput, currentState: MVVMState): Flow<FluxOutcome> {
        return when (input) {
            ChangeBackgroundInput -> ColorBackgroundState(
                getRandomColorIdUseCase.getRandomColorId(), tasks
            ).toStateOutcomeFlow().onStart { delay(1000) }
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorState("Error").toStateOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input)
            is RemoveTask -> onRemoveTask(input.fluxTask)
        }
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
