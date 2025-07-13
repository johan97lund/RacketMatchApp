package com.johan.racketmatchapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.johan.racketmatchapp.data.model.SportType

/**
 * Composable screen for application settings.
 *
 * Provides options for toggling dark mode and selecting the app's language.
 * The language selection updates the app's locale and restarts the activity
 * to apply the changes.
 *
 * @param onBack Lambda function to handle the back navigation action.
 * @param viewModel The [SettingsViewModel] instance used to manage the UI state.
 */
@Composable
fun MatchScreen(
    onBack: () -> Unit,
    sportType: SportType
) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tillbaka")
        }
        // TODO: Lägg till poängräkning och UI här
        Text(
            text = "Matchskärm kommer här",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}