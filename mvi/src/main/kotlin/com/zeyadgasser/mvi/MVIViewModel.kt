package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.FluxViewModel
import com.zeyadgasser.core.api.Result
import com.zeyadgasser.core.api.emptyResultFlow
import com.zeyadgasser.core.api.inFlow
import com.zeyadgasser.core.api.inParallelFlow
import com.zeyadgasser.core.api.toErrorResultParallelFlow
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

    override fun handleInputs(input: MVIInput, state: MVIState): Flow<Result> =
        when (input) {
            ChangeBackgroundInput -> onChangeBackground()
            ShowDialogInput -> ShowDialogEffect.inParallelFlow()
            UncaughtErrorInput -> IllegalStateException("UncaughtError").toErrorResultParallelFlow()
            NavBackInput -> NavBackEffect.inParallelFlow()
            ErrorInput -> ErrorResult("Error").inParallelFlow()
            is ChangeTaskChecked -> onChangeTaskChecked(input, state.color)
            is RemoveTask -> onRemoveTask(input.id, state.color)
            DoNothing -> emptyResultFlow()
        }

    private fun onChangeBackground(): Flow<Result> = ChangeBackgroundResult(
        getRandomColorIdUseCase.getRandomColorId(), fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
    ).inFlow().onEach { delay(1000) }.makeCancellable(ChangeBackgroundInput::class)

    private fun onRemoveTask(id: Long, color: Long): Flow<Result> =
        ChangeBackgroundResult(color, fluxTaskUseCases.removeTask(id).map { FluxTaskItem(it) }).inParallelFlow()

    private fun onChangeTaskChecked(input: ChangeTaskChecked, color: Long): Flow<Result> = ChangeBackgroundResult(
        color, fluxTaskUseCases.onChangeTaskChecked(input.id, input.checked).map { FluxTaskItem(it) }
    ).inParallelFlow()
}
