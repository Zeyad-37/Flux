package com.zeyadgasser.composables

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSiblings
import com.zeyadgasser.composables.theme.FluxTheme
import org.junit.Rule
import org.junit.Test

class FluxTaskKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fluxTask() {
        val taskName = "Task1"
        composeTestRule.setContent { FluxTheme { FluxTask(taskName) } }
        composeTestRule.onNodeWithText(taskName).assertIsDisplayed()
        composeTestRule.onNodeWithText(taskName).onSiblings()[0].assertHasClickAction()
        composeTestRule.onNodeWithText(taskName).onSiblings()[1].assertHasClickAction()
    }
}
