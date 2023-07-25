package com.zeyadgasser.flux.screens.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.metrics.performance.PerformanceMetricsState
import com.zeyadgasser.composables.theme.FluxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var jankStatsAggregator: JankStatsAggregator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FluxTheme { HomeScreen() } }
        // metrics state holder can be retrieved regardless of JankStats initialization
        PerformanceMetricsState
            .getHolderForHierarchy(window.decorView)
            .state?.putState("MainActivity", javaClass.simpleName)
        // initialize JankStats for current window
        jankStatsAggregator = JankStatsAggregator(window) { reason, totalFrames, jankFrameData ->
            Log.v(
                "MainActivity",
                "*** Jank Report ($reason), totalFrames = $totalFrames, jankFrames = ${jankFrameData.size}"
            )
            jankFrameData.forEach { frameData -> Log.v("MainActivity", frameData.toString()) }
        }
    }

    override fun onResume() {
        super.onResume()
        jankStatsAggregator.jankStats.isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        // Before disabling tracking, issue the report with (optionally) specified reason.
        jankStatsAggregator.issueJankReport("MainActivity paused")
        jankStatsAggregator.jankStats.isTrackingEnabled = false
    }
}

@SuppressWarnings("FunctionNaming")
@Preview(showBackground = true)
@Composable
fun MainPreview() = FluxTheme { HomeScreen() }
