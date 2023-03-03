package com.zeyadgasser.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.composables.theme.FluxTheme
import org.junit.Rule
import org.junit.Test

class MVScreenContentKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

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
                    { }, { }, { }, { }, { }, { }, { }, { _, _ -> },
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