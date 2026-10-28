package com.enonymous1.implicitintentdemo

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Deterministic person-screen checks only; no activity is launched. */
@RunWith(AndroidJUnit4::class)
class PersonScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyForm_showsFirstNameErrorOnSave() {
        composeRule.setContent {
            MaterialTheme {
                PersonScreen(
                    mode = PersonMode.Add,
                    onSave = {},
                    onDelete = {},
                    onClose = {},
                )
            }
        }
        composeRule.onNodeWithTag("save_button").performClick()
        composeRule.onNodeWithText("Enter a first name.").assertIsDisplayed()
    }

    @Test
    fun validForm_reportsPersonOnSave() {
        var saved: Person? = null
        composeRule.setContent {
            MaterialTheme {
                PersonScreen(
                    mode = PersonMode.Add,
                    onSave = { saved = it },
                    onDelete = {},
                    onClose = {},
                )
            }
        }
        composeRule.onNodeWithTag("first_name_field").performTextInput("Ada")
        composeRule.onNodeWithTag("last_name_field").performTextInput("Lovelace")
        composeRule.onNodeWithTag("age_field").performTextInput("36")
        composeRule.onNodeWithTag("save_button").performClick()
        assertEquals(Person("Ada", "Lovelace", 36), saved)
    }

    @Test
    fun invalidAge_doesNotReportPerson() {
        var saved: Person? = null
        composeRule.setContent {
            MaterialTheme {
                PersonScreen(
                    mode = PersonMode.Add,
                    onSave = { saved = it },
                    onDelete = {},
                    onClose = {},
                )
            }
        }
        composeRule.onNodeWithTag("first_name_field").performTextInput("Ada")
        composeRule.onNodeWithTag("last_name_field").performTextInput("Lovelace")
        composeRule.onNodeWithTag("age_field").performTextInput("151")
        composeRule.onNodeWithTag("save_button").performClick()
        assertNull(saved)
        composeRule.onNodeWithText("Enter a whole-number age from 0 to 150.").assertIsDisplayed()
    }

    @Test
    fun viewMode_showsPersonAndReportsDelete() {
        val person = Person("Ada", "Lovelace", 36)
        var deleted: Person? = null
        composeRule.setContent {
            MaterialTheme {
                PersonScreen(
                    mode = PersonMode.View(person),
                    onSave = {},
                    onDelete = { deleted = it },
                    onClose = {},
                )
            }
        }
        composeRule.onNodeWithTag("detail_first_name_text").assertIsDisplayed()
        composeRule.onNodeWithTag("detail_last_name_text").assertIsDisplayed()
        composeRule.onNodeWithTag("detail_age_text").assertIsDisplayed()
        composeRule.onNodeWithTag("delete_button").performClick()
        assertEquals(person, deleted)
    }

    @Test
    fun unsupportedMode_showsMessageAndCloses() {
        var closed = false
        composeRule.setContent {
            MaterialTheme {
                PersonScreen(
                    mode = PersonMode.Unsupported,
                    onSave = {},
                    onDelete = {},
                    onClose = { closed = true },
                )
            }
        }
        composeRule.onNodeWithTag("unsupported_text").assertIsDisplayed()
        composeRule.onNodeWithTag("close_button").performClick()
        assertTrue(closed)
    }
}
