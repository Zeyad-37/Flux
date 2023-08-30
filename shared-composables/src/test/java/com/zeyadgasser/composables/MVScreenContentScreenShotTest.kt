package com.zeyadgasser.composables

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_6
import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class MVScreenContentScreenShotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_6,
        theme = "android:Theme.Material.Light.DarkActionBar"
        // ...see docs for more options
    )

    @Test
    fun mVContentScreen(
        @TestParameter checked: Boolean,
        @TestParameter isLoading: Boolean,
        @TestParameter showDialog: Boolean
    ) {
        paparazzi.snapshot {
            MVScreenContent(
                Color.White,
                "ErrorMessage",
                "UncaughtErrorMessage",
                isLoading = isLoading,
                showDialog = showDialog,
                listOf(FluxTaskItem(1, "Label", checked)),
                LazyListState(),
                changeBackgroundOnClick = {},
                showDialogOnClick = {},
                showErrorStateOnClick = {},
                showUncaughtErrorOnClick = {},
                goBackOnClick = {},
                onDismissClick = {},
                onCloseTask = {},
                onCheckedTask = { _, _ -> },
                doNothingOnClick = {},
                cancelChangeBackgroundOnClick = {}
            )
        }
    }
}
