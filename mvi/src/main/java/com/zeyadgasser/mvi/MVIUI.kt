package com.zeyadgasser.mvi

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
fun MVIScreen(
    viewModel: MVIViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
    val outputState: ComposeState<Output> = viewModel.observe().collectAsState(Main)
    var successState: MVIState by rememberSaveable { mutableStateOf(viewModel.initialState) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var uncaughtErrorMessage by remember { mutableStateOf("") }
    when (val output = outputState.value) {
        is Effect -> {
            showDialog = (output as MVIEffect) is ShowDialogEffect
            bindEffects(output, onBackClicked)
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
        onCloseTask = { id -> viewModel.removeTask(id) },
        onCheckedTask = { id, checked -> viewModel.changeTaskChecked(id, checked) },
        doNothingOnClick = { viewModel.doNothing() },
    )
}

private fun bindEffects(effect: MVIEffect, onBackClicked: () -> Unit) = when (effect) {
    is NavBackEffect -> onBackClicked()
    is ShowDialogEffect -> Unit
}

@SuppressWarnings("FunctionNaming")
@Composable
private fun MVIState.evaluateColor() = when (this) {
    InitialState -> MaterialTheme.colors.background
    is ColorBackgroundState, is ErrorState -> Color(color)
}

private fun MVIState.evaluateErrorMessage() = when (this) {
    is ErrorState -> message
    is ColorBackgroundState, InitialState -> ""
}

private fun MVIState.evaluateList(): List<FluxTaskItem> = when (this) {
    is ErrorState, InitialState -> emptyList()
    is ColorBackgroundState -> list.toMutableStateList()
}
