package com.zeyadgasser.flux.mvi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.zeyadgasser.flux.ui.theme.FluxTheme
import dagger.hilt.android.AndroidEntryPoint

val LocalActivity: ProvidableCompositionLocal<ComponentActivity> =
    staticCompositionLocalOf { error("LocalActivity is not present") }

@AndroidEntryPoint
class MVIActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalActivity provides this) { FluxTheme { MVIScreen() } }
        }
    }
}
