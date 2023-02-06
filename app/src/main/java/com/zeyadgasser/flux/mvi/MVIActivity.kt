package com.zeyadgasser.flux.mvi

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.InputStrategy.THROTTLE
import com.zeyadgasser.core.Output
import com.zeyadgasser.core.Progress
import com.zeyadgasser.flux.ui.theme.FluxTheme

class MVIActivity : ComponentActivity() {

    private val viewModel: MVIViewModel by viewModels { MVIViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FluxTheme { MVIScreen(viewModel) } }
        lifecycleScope.launchWhenCreated { viewModel.bind(InitialState) }
    }
}

@Composable
fun MVIScreen(viewModel: MVIViewModel) {
    val context = LocalContext.current
    val state: State<Output> = viewModel.observe().collectAsState()
    var successState: MVIState by remember { mutableStateOf(InitialState) }
    var isLoading: Boolean by remember { mutableStateOf(false) }
    var errorMessage: String by remember { mutableStateOf("") }
    when (val output = state.value) {
        is Effect -> bindEffect(output as MVIEffect, context)
        is Error -> errorMessage = output.message
        is Progress -> isLoading = output.isLoading
        is com.zeyadgasser.core.State -> successState = output as MVIState
    }
    Scaffold(
        Modifier.fillMaxSize(),
        backgroundColor = when (successState) {
            InitialState -> MaterialTheme.colors.background
            is ColorBackgroundState -> Color(context.resources.getColor((successState as ColorBackgroundState).color, null))
        },
        topBar = {
            TopAppBar(Modifier.fillMaxWidth()) {
                Text(text = "MVISample", modifier = Modifier.padding(8.dp))
            }
        },
        content = { paddingValues: PaddingValues ->
            MVIScreenContent(paddingValues, viewModel, errorMessage, isLoading)
        })
}

@Composable
private fun MVIScreenContent(
    paddingValues: PaddingValues,
    viewModel: MVIViewModel,
    errorMessage: String,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { viewModel.process(ChangeBackgroundInput(), THROTTLE) }) {
            Text(text = "Change Background")
        }
        Button(onClick = { viewModel.process(ShowDialogInput) }) { Text(text = "Show Dialog") }
        Button(onClick = { viewModel.process(ErrorInput) }) { Text(text = "Show Error") }

        if (errorMessage.isNotEmpty()) Text(text = errorMessage)

        if (isLoading) CircularProgressIndicator(Modifier.size(42.dp))
    }
}

private fun bindEffect(effect: MVIEffect, context: Context) = when (effect) {
    is ShowDialogEffect -> AlertDialog.Builder(context)
        .setTitle("Dialog")
        .setMessage("Dialog effect!")
        .create().show()
}