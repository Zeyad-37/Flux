package com.zeyadgasser.mvvm

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.zeyadgasser.composables.MVScreenContent
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.Output
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.State
import com.zeyadgasser.domain.FluxTask
import kotlinx.coroutines.Dispatchers.Main
import androidx.compose.runtime.State as ComposeState

@Composable
fun MVVMScreen(
    viewModel: MVVMViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
    val outputState: ComposeState<Output> = viewModel.observe().collectAsState(Main)
    var successState: MVVMState by rememberSaveable { mutableStateOf(InitialState) }
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
        changeBackgroundOnClick = { viewModel.changeBackground() },
        showDialogOnClick = { viewModel.showDialogInput() },
        showErrorStateOnClick = { viewModel.errorInput() },
        showUncaughtErrorOnClick = { viewModel.uncaughtErrorInput() },
        goBackOnClick = { viewModel.navBackInput() },
        onDismissClick = { showDialog = false },
        list = successState.evaluateList(),
        onCloseTask = { task -> viewModel.removeTask(task) },
        onCheckedTask = { task, checked -> viewModel.changeTaskChecked(task, checked) }
    )
}

@Composable
private fun BindEffects(effect: MVVMEffect, onBackClicked: () -> Unit) = when (effect) {
    is ShowDialogEffect -> Unit
    NavBackEffect -> onBackClicked()
}

@Composable
private fun MVVMState.evaluateColor() = when (this) {
    InitialState -> MaterialTheme.colors.background
    is ColorBackgroundState, is ErrorState ->
        Color(LocalContext.current.resources.getColor(color, null))
}

@Composable
private fun MVVMState.evaluateErrorMessage() = when (this) {
    is ColorBackgroundState, InitialState -> ""
    is ErrorState -> message
}

@Composable
private fun MVVMState.evaluateList(): List<FluxTask> = when (this) {
    is ErrorState, InitialState -> emptyList()
    is ColorBackgroundState -> list.toMutableStateList()
}

