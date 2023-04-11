package com.zeyadgasser.composables

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSiblings
import com.zeyadgasser.composables.theme.FluxTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
class FluxTaskKtUnitTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out // Redirect Logcat to console
    }

    @Test
    fun fluxTask() {
        val taskName = "Task1"
        composeTestRule.setContent { FluxTheme { FluxTask(taskName) } }
        composeTestRule.onNodeWithText(taskName).assertIsDisplayed()
        composeTestRule.onNodeWithText(taskName).onSiblings()[0].assertHasClickAction()
        composeTestRule.onNodeWithText(taskName).onSiblings()[1].assertHasClickAction()
    }
}
