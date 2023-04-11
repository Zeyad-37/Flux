package com.zeyadgasser.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zeyadgasser.composables.theme.FluxTheme

@SuppressWarnings("FunctionNaming")
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

@SuppressWarnings("FunctionNaming")
@Preview(showBackground = true)
@Composable
fun FluxTaskPreview() = FluxTheme { FluxTask("Task 1") }
