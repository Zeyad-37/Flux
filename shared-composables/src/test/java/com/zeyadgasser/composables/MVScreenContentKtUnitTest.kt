package com.zeyadgasser.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSiblings
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.composables.theme.FluxTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
class MVScreenContentKtUnitTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out // Redirect Logcat to console
    }

    @Test
    fun mVScreenContent() {
//        composeTestRule.onRoot().printToLog("Flux")
        val color = Color.Black
        val errorMessage = "error"
        val uncaughtErrorMessage = "uncaught error"
        val isLoading = false
        val showDialog = false
        val taskName = "Task1"
        val list = listOf(FluxTaskItem(1, taskName))
        composeTestRule.setContent {
            FluxTheme {
                MVScreenContent(
                    color, errorMessage, uncaughtErrorMessage, isLoading, showDialog, list,
                    { }, { }, { }, { }, { }, { }, { }, { _, _ -> }, {},
                )
            }
        }
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(uncaughtErrorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(taskName).onSiblings()[0].assertHasClickAction()
        composeTestRule.onNodeWithText(taskName).onSiblings()[1].assertHasClickAction()
    }

    @Test
    fun onRecreation_stateIsRestored() {
        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent { FluxTheme {} }

        // TODO: Run actions that modify the state

        // Trigger a recreation
        restorationTester.emulateSavedInstanceStateRestore()

        // TODO: Verify that state has been correctly restored.
    }
}
