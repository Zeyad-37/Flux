package com.zeyadgasser.mvi

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.metrics.performance.PerformanceMetricsState
import com.zeyadgasser.composables.MVScreenContent
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.api.Effect
import com.zeyadgasser.core.api.Error
import com.zeyadgasser.core.api.Output
import com.zeyadgasser.core.api.Progress
import com.zeyadgasser.core.api.State
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
    var showDialog by remember { mutableStateOf(false) }
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
    val listState: LazyListState = rememberLazyListState()
    // [START compose_jank_metrics]
    val metricsStateHolder = rememberMetricsStateHolder()
    // Reporting scrolling state from compose should be done from side effect to prevent recomposition.
    LaunchedEffect(metricsStateHolder, listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolling) {
                metricsStateHolder.state?.putState("LazyList", "Scrolling")
            } else {
                metricsStateHolder.state?.removeState("LazyList")
            }
        }
    }
    MVScreenContent(
        color = successState.evaluateColor(),
        errorMessage = successState.evaluateErrorMessage(),
        uncaughtErrorMessage = uncaughtErrorMessage,
        isLoading = isLoading,
        showDialog = showDialog,
        listState = listState,
        changeBackgroundOnClick = { viewModel.process(ChangeBackgroundInput) },
        cancelChangeBackgroundOnClick = { viewModel.process(CancelChangeBackgroundInput) },
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

@Composable
fun rememberMetricsStateHolder(): PerformanceMetricsState.Holder =
    LocalView.current.let { remember(it) { PerformanceMetricsState.getHolderForHierarchy(it) } }
