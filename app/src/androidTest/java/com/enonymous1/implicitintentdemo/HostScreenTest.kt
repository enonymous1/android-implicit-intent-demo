package com.enonymous1.implicitintentdemo

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Deterministic host-screen checks only; the intent round trip is manual. */
@RunWith(AndroidJUnit4::class)
class HostScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun noPerson_addEnabledViewDisabled() {
        composeRule.setContent {
            MaterialTheme {
                HostScreen(
                    state = HostUiState(),
                    onAddPerson = {},
                    onViewPerson = {},
                )
            }
        }
        composeRule.onNodeWithTag("add_person_button").assertIsEnabled()
        composeRule.onNodeWithTag("view_person_button").assertIsNotEnabled()
        composeRule.onNodeWithTag("no_person_text").assertIsDisplayed()
    }

    @Test
    fun withPerson_showsPersonAndEnablesView() {
        val person = Person("Ada", "Lovelace", 36)
        var viewed: Person? = null
        composeRule.setContent {
            MaterialTheme {
                HostScreen(
                    state = HostUiState(person = person),
                    onAddPerson = {},
                    onViewPerson = { viewed = it },
                )
            }
        }
        composeRule.onNodeWithTag("person_name_text").assertIsDisplayed()
        composeRule.onNodeWithTag("person_age_text").assertIsDisplayed()
        composeRule.onNodeWithTag("view_person_button").assertIsEnabled()
        composeRule.onNodeWithTag("view_person_button").performClick()
        assertEquals(person, viewed)
    }

    @Test
    fun message_isShownWhenPresent() {
        composeRule.setContent {
            MaterialTheme {
                HostScreen(
                    state = HostUiState(message = "Saved Ada Lovelace, 36."),
                    onAddPerson = {},
                    onViewPerson = {},
                )
            }
        }
        composeRule.onNodeWithTag("result_message_text").assertIsDisplayed()
        composeRule.onNodeWithTag("no_person_text").assertIsDisplayed()
    }
}
