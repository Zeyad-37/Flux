package com.zeyadgasser.composables

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSiblings
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.zeyadgasser.composables.theme.FluxTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class FluxTaskKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fluxTask(@TestParameter checked: Boolean) {
        val taskName = "Task1"
        composeTestRule.setContent { FluxTheme { FluxTask(taskName, checked = checked) } }
        composeTestRule.onNodeWithText(taskName).assertIsDisplayed()
        composeTestRule.onNodeWithText(taskName).onSiblings()[0].assertHasClickAction()
        composeTestRule.onNodeWithText(taskName).onSiblings()[1].assertHasClickAction()
    }
}
