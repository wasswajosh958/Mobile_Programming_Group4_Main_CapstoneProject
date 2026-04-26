package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun splashTransitionsToHomeScreen() {
        composeRule.onNodeWithText("CBC Teachers' Toolkit").assertIsDisplayed()

        composeRule.waitUntil(timeoutMillis = 2500) {
            composeRule.onAllNodes(hasText("Welcome to CBC Teachers' Toolkit"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Welcome to CBC Teachers' Toolkit").assertIsDisplayed()
    }
}
