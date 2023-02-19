package com.zeyadgasser.flux.screens.mvi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.Output
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.State
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
fun MVScreenContent(
    color: Color,
    errorMessage: String,
    uncaughtErrorMessage: String,
    isLoading: Boolean,
    showDialog: Boolean,
    changeBackgroundOnClick: () -> Unit,
    showDialogOnClick: () -> Unit,
    showErrorStateOnClick: () -> Unit,
    showUncaughtErrorOnClick: () -> Unit,
    goBackOnClick: () -> Unit,
    onDismissClick: () -> Unit,
    list: List<FluxTask> = List(30) { i -> FluxTask(i, "Task # $i") },
    onCloseTask: (FluxTask) -> Unit = {},
    onCheckedTask: (FluxTask, Boolean) -> Unit = { _, _ -> }
) = Surface(
    modifier = Modifier.fillMaxSize(),
    color = color,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = changeBackgroundOnClick) { Text(text = "Change Background") }
        Button(onClick = showDialogOnClick) { Text(text = "Show Dialog in parallel") }
        Button(onClick = showErrorStateOnClick) { Text(text = "Show Error State") }
        Button(onClick = showUncaughtErrorOnClick) { Text(text = "Show Uncaught Error") }
        Button(onClick = goBackOnClick) { Text(text = "Go Back") }
        if (errorMessage.isNotEmpty()) Text(text = errorMessage)
        if (uncaughtErrorMessage.isNotEmpty()) Text(text = uncaughtErrorMessage)
        if (isLoading) CircularProgressIndicator(Modifier.size(42.dp))
        if (showDialog) AlertDialog(
            onDismissRequest = onDismissClick,
            confirmButton = { TextButton(onDismissClick) { Text("Confirm") } },
            dismissButton = { TextButton(onDismissClick) { Text("Dismiss") } },
            title = { Text("Dialog") },
            text = { Text("Dialog effect!") },
        )
        LazyColumn(Modifier.fillMaxWidth()) {
            items(list, { item -> item.id }) { task ->
                FluxTask(
                    taskName = task.label,
                    checked = task.checked,
                    onCheckedChange = { checked -> onCheckedTask(task, checked) },
                    onClose = { onCloseTask(task) },
                )
            }
        }
    }
}

@Composable
fun FluxTask(
    taskName: String,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onClose: () -> Unit = {},
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(
        taskName,
        Modifier
            .weight(1f)
            .padding(start = 16.dp)
    )
    Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, "Close") }
}


@Composable
private fun BindEffects(effect: MVIEffect, onBackClicked: () -> Unit) = when (effect) {
    is NavBackEffect -> onBackClicked()
    is ShowDialogEffect -> Unit
}

@Composable
private fun MVIState.evaluateColor() = when (this) {
    InitialState -> MaterialTheme.colors.background
    is ColorBackgroundState, is ErrorState ->
        Color(LocalContext.current.resources.getColor(color, null))
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
