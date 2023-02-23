package com.zeyadgasser.mvi

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
fun MVIScreen(
    viewModel: MVIViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
    val outputState: ComposeState<Output> = viewModel.observe().collectAsState(Main)
    var successState: MVIState by rememberSaveable { mutableStateOf(InitialState) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var uncaughtErrorMessage by remember { mutableStateOf("") }
    when (val output = outputState.value) {
        is Effect -> {
            showDialog = (output as MVIEffect) is ShowDialogEffect
            BindEffects(output, onBackClicked)
        }
        is Error -> uncaughtErrorMessage = output.message
        is Progress -> isLoading = output.isLoading
        is State -> {
            successState = output as MVIState
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
private fun BindEffects(effect: MVIEffect, onBackClicked: () -> Unit) = when (effect) {
    is NavBackEffect -> onBackClicked()
    is ShowDialogEffect -> Unit
}

@Composable
private fun MVIState.evaluateColor() = when (this) {
    InitialState -> MaterialTheme.colors.background
    is ColorBackgroundState, is ErrorState -> Color(color)
}

@Composable
private fun MVIState.evaluateErrorMessage() = when (this) {
    is ErrorState -> message
    is ColorBackgroundState, InitialState -> ""
}

@Composable
private fun MVIState.evaluateList(): List<FluxTask> = when (this) {
    is ErrorState, InitialState -> emptyList()
    is ColorBackgroundState -> list.toMutableStateList()
}
