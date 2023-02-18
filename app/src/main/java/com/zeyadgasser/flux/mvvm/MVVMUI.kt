package com.zeyadgasser.flux.mvvm

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.Output
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.State
import com.zeyadgasser.flux.mvi.MVScreenContent
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
        color = successState.evaluateColorFromState(),
        errorMessage = successState.evaluateErrorMessageFromState(),
        uncaughtErrorMessage = uncaughtErrorMessage,
        isLoading = isLoading,
        showDialog = showDialog,
        changeBackgroundOnClick = { viewModel.changeBackground() },
        showDialogOnClick = { viewModel.showDialogInput() },
        showErrorStateOnClick = { viewModel.errorInput() },
        showUncaughtErrorOnClick = { viewModel.uncaughtErrorInput() },
        goBackOnClick = { viewModel.navBackInput() },
        onDismissClick = { showDialog = false })
}

@Composable
private fun BindEffects(effect: MVVMEffect, onBackClicked: () -> Unit) = when (effect) {
    is ShowDialogEffect -> Unit
    NavBackEffect -> onBackClicked()
}

@Composable
private fun MVVMState.evaluateColorFromState() = when (this) {
    InitialState -> MaterialTheme.colors.background
    is ColorBackgroundState, is ErrorState ->
        Color(LocalContext.current.resources.getColor(color, null))
}

@Composable
private fun MVVMState.evaluateErrorMessageFromState() = when (this) {
    is ColorBackgroundState, InitialState -> ""
    is ErrorState -> message
}
