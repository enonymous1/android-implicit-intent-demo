package com.enonymous1.implicitintentdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * The launcher host. It never edits a person itself: [onAddPerson] and
 * [onViewPerson] are expected to send the implicit intents, and the caller
 * feeds the returned person back through the [HostViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostScreen(
    state: HostUiState,
    onAddPerson: () -> Unit,
    onViewPerson: (Person) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.host_heading),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(R.string.host_body),
                style = MaterialTheme.typography.bodyMedium,
            )

            val person = state.person
            if (person == null) {
                Text(
                    text = stringResource(R.string.no_person),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("no_person_text"),
                )
            } else {
                Text(
                    text = person.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.testTag("person_name_text"),
                )
                Text(
                    text = person.age.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("person_age_text"),
                )
            }

            state.message?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("result_message_text"),
                )
            }

            Button(
                onClick = onAddPerson,
                modifier = Modifier.fillMaxWidth().testTag("add_person_button"),
            ) {
                Text(stringResource(R.string.add_person))
            }
            OutlinedButton(
                onClick = { person?.let(onViewPerson) },
                enabled = person != null,
                modifier = Modifier.fillMaxWidth().testTag("view_person_button"),
            ) {
                Text(stringResource(R.string.view_person))
            }
        }
    }
}
