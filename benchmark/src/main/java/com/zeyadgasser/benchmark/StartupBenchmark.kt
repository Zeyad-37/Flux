package com.zeyadgasser.benchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import junit.framework.TestCase.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupColdPartial() = benchmarkRule.measureRepeated(
        packageName = "com.zeyadgasser.flux",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Partial(),
        setupBlock = {}
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun startupWarmPartial() = benchmarkRule.measureRepeated(
        packageName = "com.zeyadgasser.flux",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.Partial(),
        setupBlock = {}
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun startupHotPartial() = benchmarkRule.measureRepeated(
        packageName = "com.zeyadgasser.flux",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.HOT,
        compilationMode = CompilationMode.Partial(),
        setupBlock = {}
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun startupColdFull() = benchmarkRule.measureRepeated(
        packageName = "com.zeyadgasser.flux",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Full(),
        setupBlock = {}
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun startupWarmFull() = benchmarkRule.measureRepeated(
        packageName = "com.zeyadgasser.flux",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.Full(),
        setupBlock = {}
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun startupHotFull() = benchmarkRule.measureRepeated(
        packageName = "com.zeyadgasser.flux",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.HOT,
        compilationMode = CompilationMode.Full(),
        setupBlock = {}
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun scrollList() { // TODO
        benchmarkRule.measureRepeated(
            packageName = "com.zeyadgasser.flux",
            metrics = listOf(StartupTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.HOT,
            compilationMode = CompilationMode.Full(),
            setupBlock = {
                // Before starting to measure, navigate to the UI to be measured
                val intent = Intent("$packageName.RECYCLER_VIEW_ACTIVITY")
                startActivityAndWait(intent)

                val selector = By.res(packageName, "launchRecyclerActivity")
                if (!device.wait(Until.hasObject(selector), 5_500)) {
                    fail("Could not find resource in time")
                }
                val launchRecyclerActivity = device.findObject(selector)
                launchRecyclerActivity.click()

                // wait until the activity is shown
                device.wait(
                    Until.hasObject(By.clazz("$packageName.NonExportedRecyclerActivity")),
                    TimeUnit.SECONDS.toMillis(10)
                )
            }
        ) {
            val recycler = device.findObject(By.res(packageName, "recycler"))
            // Set gesture margin to avoid triggering gesture navigation
            // with input events from automation.
            recycler.setGestureMargin(device.displayWidth / 5)

            // Scroll down several times
            repeat(3) { recycler.fling(Direction.DOWN) }
        }
    }
}
