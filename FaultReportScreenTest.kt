package com.uedcl.power.presentation.faults

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.uedcl.power.presentation.theme.UedclTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FaultReportScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFaultSubmissionInputsAndCallback() {
        var capturedDescription = ""

        // Set up the composable under test inside the Compose rule
        composeTestRule.setContent {
            UedclTheme {
                FaultReportScreen(
                    onSubmitClicked = { capturedDescription = it }
                )
            }
        }

        // 1. Verify title is displayed correctly
        composeTestRule.onNodeWithText("Report Grid Fault").assertIsDisplayed()

        // 2. Find the input field by its testTag and type text into it
        composeTestRule.onNodeWithTag("fault_input_field")
            .performTextInput("Transformer sparking near Ntinda stage")

        // 3. Find the submit button and perform a click
        composeTestRule.onNodeWithTag("submit_fault_button")
            .performClick()

        // 4. Assert that the callback received the correct typed string
        assertEquals("Transformer sparking near Ntinda stage", capturedDescription)
    }

    @Test
    fun testSubmitButtonDisplaysCorrectText() {
        composeTestRule.setContent {
            UedclTheme {
                FaultReportScreen(onSubmitClicked = {})
            }
        }

        // Assert that the submit button node has the correct text label
        composeTestRule.onNodeWithTag("submit_fault_button")
            .assertHasText("Submit Report")
            .assertIsEnabled()
    }
}
