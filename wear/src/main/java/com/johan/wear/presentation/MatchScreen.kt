package com.johan.wear.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.core.scoring.padel.ScoringEvent
import androidx.wear.compose.material.*

@Composable
fun MatchScreen(
    viewModel: MatchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showTiebreakPrompt = remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is ScoringEvent.ShowTiebreakPrompt) {
                showTiebreakPrompt.value = true
            }
        }
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("P1", fontSize = 12.sp)
                    Text(uiState.p1Sets, fontSize = 14.sp, color = MaterialTheme.colors.secondary)
                    Text(uiState.p1Games, fontSize = 16.sp)
                    Button(
                        onClick = { viewModel.scoreP1() },
                        modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
                    ) {
                        Text(uiState.p1Score, fontSize = 20.sp)
                    }
                }

                Text("vs", fontSize = 12.sp)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("P2", fontSize = 12.sp)
                    Text(uiState.p2Sets, fontSize = 14.sp, color = MaterialTheme.colors.secondary)
                    Text(uiState.p2Games, fontSize = 16.sp)
                    Button(
                        onClick = { viewModel.scoreP2() },
                        modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
                    ) {
                        Text(uiState.p2Score, fontSize = 20.sp)
                    }
                }
            }
        }
        
        if (uiState.canUndo) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.undo() },
                    modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Text("Undo", fontSize = 10.sp)
                }
            }
        }
    }

    if (showTiebreakPrompt.value) {
        AlertDialog(
            onDismissRequest = { showTiebreakPrompt.value = false },
            title = { Text("Start tiebreak?") },
            message = { Text("It's 6–6. Start a tiebreak?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.decideTieBreak(start = true)
                    showTiebreakPrompt.value = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.decideTieBreak(start = false)
                    showTiebreakPrompt.value = false
                }) {
                    Text("No")
                }
            }
        )
    }
}
