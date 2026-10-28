package com.enonymous1.implicitintentdemo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HostUiState(
    val person: Person? = null,
    val message: String? = null,
)

/**
 * Owns the host screen's person across rotation and process death. Neither
 * screen stores anything: the host only keeps the last person a result
 * intent returned, in [SavedStateHandle], until it is deleted or replaced.
 */
class HostViewModel(
    private val savedStateHandle: SavedStateHandle = SavedStateHandle(),
) : ViewModel() {

    companion object {
        private const val KEY_FIRST_NAME = "host_first_name"
        private const val KEY_LAST_NAME = "host_last_name"
        private const val KEY_AGE = "host_age"
    }

    private val _uiState = MutableStateFlow(
        HostUiState(person = savedPerson()),
    )
    val uiState: StateFlow<HostUiState> = _uiState.asStateFlow()

    private fun savedPerson(): Person? {
        val firstName: String? = savedStateHandle[KEY_FIRST_NAME]
        val lastName: String? = savedStateHandle[KEY_LAST_NAME]
        val age: Int? = savedStateHandle[KEY_AGE]
        return personFromValues(firstName, lastName, age)
    }

    private fun savePerson(person: Person?) {
        if (person == null) {
            savedStateHandle.remove<String>(KEY_FIRST_NAME)
            savedStateHandle.remove<String>(KEY_LAST_NAME)
            savedStateHandle.remove<Int>(KEY_AGE)
        } else {
            savedStateHandle[KEY_FIRST_NAME] = person.firstName
            savedStateHandle[KEY_LAST_NAME] = person.lastName
            savedStateHandle[KEY_AGE] = person.age
        }
    }

    /** The add-person screen returned a person: keep it and say so. */
    fun onPersonSaved(person: Person) {
        savePerson(person)
        _uiState.update {
            it.copy(
                person = person,
                message = "Saved ${person.displayName}, ${person.age}.",
            )
        }
    }

    /** The view-person screen returned the person to delete: drop it. */
    fun onPersonDeleted(person: Person) {
        savePerson(null)
        _uiState.update {
            it.copy(
                person = null,
                message = "Deleted ${person.displayName}.",
            )
        }
    }
}
