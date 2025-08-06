package com.johan.racketmatchapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color

/**
 * Composable screen for the main menu of the application.
 *
 * Displays a centered button for starting a new match and a settings button
 * positioned at the bottom-left corner. The settings button includes an icon
 * and text for navigation to the settings screen.
 *
 * @param onMatchClick Lambda function to handle the "New Match" button click.
 * @param onSettingsClick Lambda function to handle the "Settings" button click.
 */
@Composable
fun MainScreen(
    onMatchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)


        ) {

            // CENTRERAD STOR "Ny Match"-knapp
            Button(
                onClick = onMatchClick,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.7f)
                    .height(80.dp)
            ) {
                Text(
                    text = "Ny Match",
                    style = MaterialTheme.typography.titleLarge
                )

            }
        }
    }
}
