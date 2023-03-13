package com.zeyadgasser.mvvm

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.zeyadgasser.composables.MVScreenContent
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.Output
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.State
import kotlinx.coroutines.Dispatchers.Main
import androidx.compose.runtime.State as ComposeState

@SuppressWarnings("FunctionNaming")
@Composable
fun MVVMScreen(
    viewModel: MVVMViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
) {
    val outputState: ComposeState<Output> = viewModel.observe().collectAsState(Main)
    var successState: MVVMState by rememberSaveable { mutableStateOf(viewModel.initialState) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var uncaughtErrorMessage by remember { mutableStateOf("") }
    when (val output = outputState.value) {
        is Effect -> {
            showDialog = (output as MVVMEffect) is ShowDialogEffect
            BindEffects(output, onBackClicked)
        }
        is Error -> uncaughtErrorMessage = output.message
        is Progress -> isLoading = output.isLoading
        is State -> {
            successState = output as MVVMState
            uncaughtErrorMessage = ""
        }
    }
    MVScreenContent(
        color = successState.evaluateColor(),
        errorMessage = successState.evaluateErrorMessage(),
        uncaughtErrorMessage = uncaughtErrorMessage,
        isLoading = isLoading,
        showDialog = showDialog,
        changeBackgroundOnClick = { viewModel.process(ChangeBackgroundInput) },
        showDialogOnClick = { viewModel.process(ShowDialogInput) },
        showErrorStateOnClick = { viewModel.process(ErrorInput) },
        showUncaughtErrorOnClick = { viewModel.process(UncaughtErrorInput) },
        goBackOnClick = { viewModel.process(NavBackInput) },
        onDismissClick = { showDialog = false },
        list = successState.evaluateList(),
        onCloseTask = { id -> viewModel.process(RemoveTask(id)) },
        onCheckedTask = { id, checked -> viewModel.process(ChangeTaskChecked(id, checked)) },
        doNothingOnClick = { viewModel.process(DoNothing) },
    )
}

@SuppressWarnings("FunctionNaming")
@Composable
private fun BindEffects(effect: MVVMEffect, onBackClicked: () -> Unit) = when (effect) {
    is ShowDialogEffect -> Unit
    NavBackEffect -> onBackClicked()
}

@Composable
private fun MVVMState.evaluateColor() = when (this) {
    InitialState -> MaterialTheme.colors.background
    is ColorBackgroundState, is ErrorState -> Color(color)
}

private fun MVVMState.evaluateErrorMessage() = when (this) {
    is ColorBackgroundState, InitialState -> ""
    is ErrorState -> message
}

private fun MVVMState.evaluateList(): List<FluxTaskItem> = when (this) {
    is ErrorState, InitialState -> emptyList()
    is ColorBackgroundState -> list.toMutableStateList()
}

