package com.enonymous1.implicitintentdemo

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HostViewModelTest {

    @Test
    fun savedPerson_isKeptAndReported() {
        val viewModel = HostViewModel()
        val person = Person("Ada", "Lovelace", 36)
        viewModel.onPersonSaved(person)
        assertEquals(person, viewModel.uiState.value.person)
        assertEquals("Saved Ada Lovelace, 36.", viewModel.uiState.value.message)
    }

    @Test
    fun deletedPerson_isDroppedAndReported() {
        val viewModel = HostViewModel()
        val person = Person("Ada", "Lovelace", 36)
        viewModel.onPersonSaved(person)
        viewModel.onPersonDeleted(person)
        assertNull(viewModel.uiState.value.person)
        assertEquals("Deleted Ada Lovelace.", viewModel.uiState.value.message)
    }

    @Test
    fun savedPerson_isRestoredFromSavedState() {
        val savedStateHandle = SavedStateHandle(
            initialState = mapOf(
                "host_first_name" to "Ada",
                "host_last_name" to "Lovelace",
                "host_age" to 36,
            ),
        )
        val viewModel = HostViewModel(savedStateHandle)
        assertEquals(Person("Ada", "Lovelace", 36), viewModel.uiState.value.person)
    }

    @Test
    fun incompleteSavedState_restoresNoPerson() {
        val savedStateHandle = SavedStateHandle(
            initialState = mapOf("host_first_name" to "Ada"),
        )
        val viewModel = HostViewModel(savedStateHandle)
        assertNull(viewModel.uiState.value.person)
    }
}
