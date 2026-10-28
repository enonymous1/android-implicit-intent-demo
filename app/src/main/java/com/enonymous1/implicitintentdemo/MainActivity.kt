package com.enonymous1.implicitintentdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {

    private val hostViewModel: HostViewModel by viewModels()

    private val addPersonLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.personOrNull()?.let(hostViewModel::onPersonSaved)
        }
    }

    private val viewPersonLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.personOrNull()?.let(hostViewModel::onPersonDeleted)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val state by hostViewModel.uiState.collectAsStateWithLifecycle()
                    HostScreen(
                        state = state,
                        onAddPerson = {
                            addPersonLauncher.launch(
                                Intent(PersonContract.ACTION_ADD_PERSON),
                            )
                        },
                        onViewPerson = { person ->
                            viewPersonLauncher.launch(
                                Intent(PersonContract.ACTION_VIEW_PERSON)
                                    .withPerson(person),
                            )
                        },
                    )
                }
            }
        }
    }
}
