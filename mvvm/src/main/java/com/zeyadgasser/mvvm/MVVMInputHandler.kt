package com.zeyadgasser.mvvm

import com.zeyadgasser.core.*
import com.zeyadgasser.domain.CheckableItem
import com.zeyadgasser.domain.FluxTask
import com.zeyadgasser.domain.FluxTaskUseCases
import com.zeyadgasser.domain.GetRandomColorIdUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MVVMInputHandler @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
) : InputHandler<MVVMInput, MVVMState> {

    private val tasks = MutableList(30) { i -> FluxTask(i.toLong(), "Task # $i") }

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
        takeIf { fluxTaskUseCases.removeTask(tasks as MutableList<CheckableItem>, task) }
            ?.run { ColorBackgroundState(getRandomColorIdUseCase.getRandomColorId(), tasks) }
            ?.toStateOutcomeFlow()
            ?: Exception("Couldn't remove task from list").toErrorOutcomeFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked): Flow<FluxOutcome> =
        fluxTaskUseCases.onChangeTaskChecked(tasks, input.fluxTask, input.checked)
            .run { ColorBackgroundState(getRandomColorIdUseCase.getRandomColorId(), tasks) }
            .toStateOutcomeFlow()
}
