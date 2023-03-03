package com.zeyadgasser.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zeyadgasser.composables.presentationModels.FluxTaskItem

@SuppressWarnings("FunctionNaming", "LongParameterList")
@Composable
fun MVScreenContent(
    color: Color,
    errorMessage: String,
    uncaughtErrorMessage: String,
    isLoading: Boolean,
    showDialog: Boolean,
    list: List<FluxTaskItem>,
    changeBackgroundOnClick: () -> Unit,
    showDialogOnClick: () -> Unit,
    showErrorStateOnClick: () -> Unit,
    showUncaughtErrorOnClick: () -> Unit,
    goBackOnClick: () -> Unit,
    onDismissClick: () -> Unit,
    onCloseTask: (Long) -> Unit,
    onCheckedTask: (Long, Boolean) -> Unit,
    doNothingOnClick: () -> Unit,
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
        Button(onClick = doNothingOnClick) { Text(text = "Do nothing!") }
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
                    onCheckedChange = { checked -> onCheckedTask(task.id, checked) },
                    onClose = { onCloseTask(task.id) },
                )
            }
        }
    }
}
