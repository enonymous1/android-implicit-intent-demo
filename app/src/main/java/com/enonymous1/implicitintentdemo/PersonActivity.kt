package com.enonymous1.implicitintentdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

/**
 * Answers the implicit intents. The launching action decides the screen,
 * and a missing or unrecognised action shows the unsupported state
 * instead of the original's null-action crash. Nothing is saved here:
 * save and delete both return the person as a result intent, exactly as
 * the original did, and backing out returns no result.
 */
class PersonActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mode = resolveMode(intent)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PersonScreen(
                        mode = mode,
                        onSave = { person -> finishWithPerson(person) },
                        onDelete = { person -> finishWithPerson(person) },
                        onClose = { finish() },
                    )
                }
            }
        }
    }

    private fun resolveMode(intent: Intent): PersonMode {
        val action = intent.action
        return when {
            PersonContract.isAddAction(action) -> PersonMode.Add
            PersonContract.isViewAction(action) -> {
                val person = intent.personOrNull()
                if (person == null) PersonMode.Unsupported else PersonMode.View(person)
            }
            else -> PersonMode.Unsupported
        }
    }

    private fun finishWithPerson(person: Person) {
        setResult(Activity.RESULT_OK, Intent().withPerson(person))
        finish()
    }
}
