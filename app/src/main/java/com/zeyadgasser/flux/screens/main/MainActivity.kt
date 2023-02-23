package com.zeyadgasser.flux.screens.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.zeyadgasser.composables.theme.FluxTheme

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FluxTheme { HomeScreen() } }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() = FluxTheme { HomeScreen() }
