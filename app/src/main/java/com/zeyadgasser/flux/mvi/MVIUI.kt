package com.zeyadgasser.flux.mvi

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.Output
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.State
import kotlinx.coroutines.Dispatchers.Main
import androidx.compose.runtime.State as ComposeState

@Composable
fun MVIScreen(viewModel: MVIViewModel = viewModel(factory = MVIViewModel.Factory)) {
    val outputState: ComposeState<Output> = viewModel.observe().collectAsState(Main)
    var successState: MVIState by rememberSaveable { mutableStateOf(InitialState) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var uncaughtErrorMessage by remember { mutableStateOf("") }
    when (val output = outputState.value) {
        is Effect -> {
            showDialog = (output as MVIEffect) is ShowDialogEffect
            BindEffects(output)
        }
        is Error -> uncaughtErrorMessage = output.message
        is Progress -> isLoading = output.isLoading
        is State -> {
            successState = output as MVIState
            uncaughtErrorMessage = ""
        }
    }
    MVIScreenScaffold(
        successState,
        isLoading,
        showDialog,
        uncaughtErrorMessage,
        { viewModel.changeBackground() },
        { viewModel.showDialogInput() },
        { viewModel.errorInput() },
        { viewModel.uncaughtErrorInput() },
        { viewModel.navBackInput() },
        { showDialog = false },
    )
}

@Composable
private fun BindEffects(effect: MVIEffect) = when (effect) {
    is NavBackEffect -> LocalActivity.current.onBackPressed()
    is ShowDialogEffect -> Unit
}

@Composable
fun MVIScreenScaffold(
    successState: MVIState,
    isLoading: Boolean,
    showDialog: Boolean,
    uncaughtErrorMessage: String,
    changeBackgroundOnClick: () -> Unit,
    showDialogOnClick: () -> Unit,
    showErrorStateOnClick: () -> Unit,
    showUncaughtErrorOnClick: () -> Unit,
    goBackOnClick: () -> Unit,
    onDismissClick: () -> Unit,
) = Scaffold(
    Modifier.fillMaxSize(),
    backgroundColor = successState.evaluateColorFromState(),
    topBar = { TopAppBar(Modifier.fillMaxWidth()) { Text("MVISample", Modifier.padding(8.dp)) } },
    content = { paddingValues: PaddingValues ->
        MVIScreenContent(
            errorMessage = successState.evaluateErrorMessageFromState(),
            uncaughtErrorMessage = uncaughtErrorMessage,
            isLoading = isLoading,
            showDialog = showDialog,
            paddingValues = paddingValues,
            changeBackgroundOnClick = changeBackgroundOnClick,
            showDialogOnClick = showDialogOnClick,
            showErrorStateOnClick = showErrorStateOnClick,
            showUncaughtErrorOnClick = showUncaughtErrorOnClick,
            goBackOnClick = goBackOnClick,
            onDismissClick = onDismissClick,
        )
    })

@Composable
private fun MVIScreenContent(
    errorMessage: String,
    uncaughtErrorMessage: String,
    isLoading: Boolean,
    showDialog: Boolean,
    paddingValues: PaddingValues,
    changeBackgroundOnClick: () -> Unit,
    showDialogOnClick: () -> Unit,
    showErrorStateOnClick: () -> Unit,
    showUncaughtErrorOnClick: () -> Unit,
    goBackOnClick: () -> Unit,
    onDismissClick: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
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
}

@Composable
private fun MVIState.evaluateColorFromState() = when (this) {
    InitialState -> MaterialTheme.colors.background
    is ColorBackgroundState, is ErrorState -> Color(
        LocalContext.current.resources.getColor(
            color,
            null
        )
    )
}

@Composable
private fun MVIState.evaluateErrorMessageFromState() = when (this) {
    is ErrorState -> message
    is ColorBackgroundState, InitialState -> ""
}
