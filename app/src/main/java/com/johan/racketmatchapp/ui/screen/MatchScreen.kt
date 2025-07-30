package com.johan.racketmatchapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.data.model.SportType
import com.johan.racketmatchapp.settings.MatchScreenViewModel
import com.johan.racketmatchapp.settings.MatchScreenVmFactory

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

    val viewModel: MatchScreenViewModel = viewModel(
        factory = MatchScreenVmFactory(sportType)
    )

    val state by viewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tillbaka")
        }

        Column(
            Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = state.user1,
                onValueChange = viewModel::setUser1,
                label = { Text("Player 1 name") },
                singleLine = true
            )

            TextField(
                value = state.user2,
                onValueChange = viewModel::setUser2,
                label = { Text("Player 2 name") },
                singleLine = true
            )
        }
    }
}

@Preview
@Composable
fun app(){
    MatchScreen(onBack = {}, sportType = SportType.PICKLEBALL)
}