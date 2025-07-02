package com.johan.racketmatchapp.ui.screen

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.settings.SettingsViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLang by viewModel.languageState.collectAsState()
    val context = LocalContext.current

    // Språk-lista
    val languages = listOf(
        "sv" to "Svenska",
        "en" to "English",
        "es" to "Español"
    )

    // När språk ändras: uppdatera locale och återstarta aktiviteten
    LaunchedEffect(currentLang) {
        val locale = java.util.Locale(currentLang)
        java.util.Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        (context as? Activity)?.recreate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inställningar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Tillbaka")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Tema-val
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mörkt Läge")
                Switch(
                    checked = uiState.darkMode,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }

            // Språk-val med dropdown
            var expanded by remember { mutableStateOf(false) }
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(8.dp)
                ) {
                    Text("Språk: ${languages.first { it.first == currentLang }.second}")
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.selectLanguage(code)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
