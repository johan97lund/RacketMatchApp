package com.johan.racketmatchapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onMatchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onMatchClick,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Starta Match")
        }
        Button(
            onClick = onSettingsClick,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Inställningar")
        }
    }
}