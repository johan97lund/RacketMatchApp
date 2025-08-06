package com.johan.racketmatchapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import com.johan.racketmatchapp.core.data.model.SportType

/**
 * Composable screen that allows the user to select a sport type.
 *
 * Displays a centered title and a list of buttons, one for each available [SportType].
 * When a button is pressed, the [onSelect] callback is invoked with the selected sport.
 *
 * @param onSelect Lambda function to handle the selected [SportType].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportSelectionScreen(
    onSelect: (SportType) -> Unit,
    onBack: () -> Unit,
    ) {
    Surface(modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Box (modifier = Modifier.padding(24.dp)){
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Tillbaka"
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Centrerad rubrik ovanför knapparna
                Text(
                    text = "Välj sport",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                SportType.entries.forEach { sport ->
                    SportButton(sport) { onSelect(sport) }
                }

            }
        }
    }
}

@Composable
private fun SportButton(
    sport: SportType,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(56.dp)
    ) {
        Text(
            text = sport.displayName,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SportSelectionScreenPreview() {
    SportSelectionScreen(onSelect = {}, onBack = {println("tjena")})
}
