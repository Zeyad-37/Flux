package com.zeyadgasser.flux

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.zeyadgasser.flux.mvi.MVIActivity
import com.zeyadgasser.flux.mvvm.MVVMActivity
import com.zeyadgasser.flux.ui.theme.FluxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FluxTheme { HomeScreen() }
        }
    }
}

@Composable
fun Context.HomeScreen() {
    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(Modifier.padding(PaddingValues(Dp(28f)))) {
            Greeting("Flux Samples")
            Button(onClick = {
                startActivity(Intent(this@HomeScreen, MVIActivity::class.java))
            }) { Text(text = "MVI Sample") }
            Button(onClick = {
                startActivity(Intent(this@HomeScreen, MVVMActivity::class.java))
            }) { Text(text = "MVVM Sample") }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FluxTheme {
        Greeting("Android")
    }
}