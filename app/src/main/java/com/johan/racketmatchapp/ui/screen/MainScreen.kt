package com.johan.racketmatchapp.ui.screen

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
import androidx.compose.material3.TextButton

@Composable
fun MainScreen(
    onMatchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // CENTRERAD STOR "Starta Match"-knapp
        Button(
            onClick = onMatchClick,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.7f)
                .height(80.dp)
        ) {
            Text(
                text = "Starta Match",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // INSTÄLLNINGAR nere till vänster med ikon
        TextButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Inställningar"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Inställningar")
        }
    }
}
