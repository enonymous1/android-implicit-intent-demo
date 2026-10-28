package com.enonymous1.implicitintentdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/** Which screen the launching intent asked for, resolved in [PersonActivity]. */
sealed interface PersonMode {
    data object Add : PersonMode
    data class View(val person: Person) : PersonMode
    data object Unsupported : PersonMode
}

/**
 * The form and details screens from the original exercise, in Compose.
 * The form keeps the original rule — it never saves anything itself —
 * and reports a valid person through [onSave] for the caller to keep.
 * The details screen likewise reports through [onDelete]. An intent with
 * no recognised action, or a view intent with no usable person, lands in
 * [PersonMode.Unsupported] instead of crashing, as the original did.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
    mode: PersonMode,
    onSave: (Person) -> Unit,
    onDelete: (Person) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val title = when (mode) {
        PersonMode.Add -> stringResource(R.string.add_person)
        is PersonMode.View -> stringResource(R.string.view_person)
        PersonMode.Unsupported -> stringResource(R.string.app_name)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = {
                    when (mode) {
                        PersonMode.Add -> {
                            TextButton(
                                onClick = {
                                    when (val parsed = parsePerson(firstName, lastName, age)) {
                                        is PersonParse.Valid -> onSave(parsed.person)
                                        is PersonParse.Invalid -> error = parsed.reason
                                    }
                                },
                                modifier = Modifier.testTag("save_button"),
                            ) {
                                Text(stringResource(R.string.save))
                            }
                        }
                        is PersonMode.View -> {
                            TextButton(
                                onClick = { onDelete(mode.person) },
                                modifier = Modifier.testTag("delete_button"),
                            ) {
                                Text(stringResource(R.string.delete))
                            }
                        }
                        PersonMode.Unsupported -> Unit
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (mode) {
                PersonMode.Add -> {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it; error = null },
                        label = { Text(stringResource(R.string.first_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("first_name_field"),
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it; error = null },
                        label = { Text(stringResource(R.string.last_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("last_name_field"),
                    )
                    OutlinedTextField(
                        value = age,
                        onValueChange = {
                            age = it.filter { char -> char.isDigit() }.take(3)
                            error = null
                        },
                        label = { Text(stringResource(R.string.age)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("age_field"),
                    )
                    error?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("form_error_text"),
                        )
                    }
                }
                is PersonMode.View -> {
                    val person = mode.person
                    Text(
                        text = stringResource(R.string.first_name),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = person.firstName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.testTag("detail_first_name_text"),
                    )
                    Text(
                        text = stringResource(R.string.last_name),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = person.lastName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.testTag("detail_last_name_text"),
                    )
                    Text(
                        text = stringResource(R.string.age),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = person.age.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.testTag("detail_age_text"),
                    )
                }
                PersonMode.Unsupported -> {
                    Text(
                        text = stringResource(R.string.unsupported_intent),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("unsupported_text"),
                    )
                    Button(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_button"),
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}
