package com.johan.racketmatchapp.ui.screen

import java.util.Locale
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import com.johan.racketmatchapp.settings.AppLanguage
import androidx.core.os.LocaleListCompat



/**
 * Composable screen for application settings.
 *
 * TODO - Implement more user settings and document them.
 * Provides options for toggling dark mode and selecting the app's language.
 * The language selection updates the app's locale and restarts the activity
 * to apply the changes.
 *
 * @param onBack Lambda function to handle the back navigation action.
 * @param viewModel The [SettingsViewModel] instance used to manage the UI state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    /* ---- side‑effect: switch app locale when the enum changes ---- */
    LaunchedEffect(uiState.language) {
        val tags = uiState.language.name.lowercase()
        val locales = LocaleListCompat.forLanguageTags(tags)

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inställningar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tillbaka"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            /* -------- dark‑mode toggle -------- */
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mörkt läge")
                Switch(
                    checked = uiState.darkMode,
                    onCheckedChange = viewModel::setDarkMode
                )
            }

            /* -------- language picker -------- */
            var expanded by remember { mutableStateOf(false) }

            Box {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Språk: ${uiState.language.displayName}")
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                    AppLanguage.values().forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang.displayName) },
                            onClick = {
                                viewModel.setLanguage(lang)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}