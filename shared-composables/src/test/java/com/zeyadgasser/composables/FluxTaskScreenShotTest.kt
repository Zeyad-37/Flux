package com.zeyadgasser.composables

import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class FluxTaskScreenShotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
    )

    @Test
    fun fluxTask(@TestParameter checked: Boolean) {
        paparazzi.snapshot {
            FluxTask("Task 1", Modifier, checked, onCheckedChange = {}, onClose = {})
        }
    }
}
