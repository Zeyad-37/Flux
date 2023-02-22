package com.zeyadgasser.mvi

import com.zeyadgasser.core.*
import com.zeyadgasser.domain.CheckableItem
import com.zeyadgasser.domain.FluxTask
import com.zeyadgasser.domain.FluxTaskUseCases
import com.zeyadgasser.domain.GetRandomColorIdUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MVIInputHandler @Inject constructor(
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase,
    private val fluxTaskUseCases: FluxTaskUseCases,
) : InputHandler<MVIInput, MVIState> {

    private val tasks = MutableList(30) { i -> FluxTask(i.toLong(), "Task # $i") }

    override fun handleInputs(input: MVIInput, currentState: MVIState): Flow<FluxOutcome> =
        when (input) {
            ChangeBackgroundInput -> ChangeBackgroundResult(
                getRandomColorIdUseCase.getRandomColorId(), tasks
            ).toResultOutcomeFlow().onStart { delay(1373) }
            ShowDialogInput -> ShowDialogEffect.toEffectOutcomeFlow().executeInParallel()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorOutcomeFlow()
            NavBackInput -> NavBackEffect.toEffectOutcomeFlow()
            ErrorInput -> ErrorResult("Error").toResultOutcomeFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input, currentState)
            is RemoveTask -> onRemoveTask(input.fluxTask)
        }

    private fun onRemoveTask(task: FluxTask): Flow<FluxOutcome> =
        takeIf { fluxTaskUseCases.removeTask((tasks as MutableList<CheckableItem>), task) }?.run {
            ChangeBackgroundResult(getRandomColorIdUseCase.getRandomColorId(), tasks)
        }?.toResultOutcomeFlow() ?: Exception("Couldn't remove task from list").toErrorOutcomeFlow()

    private fun onChangeTaskChecked(
        input: ChangeTaskChecked, currentState: MVIState
    ): Flow<FluxOutcome> =
        fluxTaskUseCases.onChangeTaskChecked(tasks, input.fluxTask, input.checked)
            .run { ChangeBackgroundResult(currentState.color, tasks) }.toResultOutcomeFlow()
}
