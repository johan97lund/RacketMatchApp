package com.johan.racketmatchapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.data.model.SportType
import com.johan.racketmatchapp.settings.GameEvent
import com.johan.racketmatchapp.settings.MatchScreenData
import com.johan.racketmatchapp.settings.MatchScreenViewModel
import com.johan.racketmatchapp.settings.MatchScreenVmFactory
import kotlinx.coroutines.flow.collectLatest


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
    val vm: MatchScreenViewModel = viewModel(
        factory = MatchScreenVmFactory(sportType)
    )
    val state by vm.uiState.collectAsState()
    val tieBreak = remember { mutableStateOf(false) }
    val gameOver = remember { mutableStateOf(false) }


    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp)) {

            if (!state.namesSet) {
                Names(
                    onBack   = onBack,
                    uiState  = state,
                    setName1 = vm::setUser1,
                    setName2 = vm::setUser2,
                    setSet = vm::setNamesSet
                )
            } else {
                ScoreBoard(
                    onBack = onBack,
                    uiState = state,
                    incP1 = vm::incP1, decP1 = vm::decP1,
                    incP2 = vm::incP2, decP2 = vm::decP2
                )
            }
        }
    }

    LaunchedEffect(vm) {
        vm.events1.collectLatest { event ->
            val s = vm.uiState.value
            when (event) {
                is GameEvent.TieBreak -> {
                    tieBreak.value = true
                }
                is GameEvent.GameOver -> {
                    gameOver.value = true
                }
                else -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showFor(event, s)
                }
            }
        }
    }


    if (tieBreak.value) { // Check the value of the MutableState
        TieBreakDialog(
            onBack = onBack,
            confirm = {
                vm.startTieBreak()
                tieBreak.value = false // Also dismiss dialog on confirm
            },
            tieBreakState = tieBreak
        )
    }


    if (gameOver.value){
        GameOverDialog("jag", onBack)
    }



}

private suspend fun SnackbarHostState.showFor(
    event: GameEvent,
    state: MatchScreenData
) {
    currentSnackbarData?.dismiss()
    fun who(p: Int) = if (p == 0) state.user1 else state.user2

    when (event) {
        is GameEvent.Score         -> showSnackbar("Point for ${who(event.player)}")
        is GameEvent.UndoScore     -> showSnackbar("Undid a point for ${who(event.player)}")
        is GameEvent.Advantage     -> showSnackbar("Advantage ${who(event.player)}")
        is GameEvent.Deuce         -> showSnackbar("Deuce")
        is GameEvent.PaddelGameWon -> showSnackbar("${who(event.player)} won game")
        is GameEvent.SetScore      -> showSnackbar("${who(event.player)} won the set")
        is GameEvent.TieBreak      -> showSnackbar("Tiebreak — ${who(event.player)} to serve")
        is GameEvent.GameOver -> {
            val winner = who(event.player)
            showSnackbar(
                message = "Game over — $winner wins ${state.p1Display}-${state.p2Display}",
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
        }
    }
}

@Composable
private fun TieBreakDialog(
    onBack: () -> Unit,
    confirm: () -> Unit,
    tieBreakState: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = { tieBreakState.value = false },
        title = { Text("Start tiebreak?") },
        text = { Text("It's 6–6. Start a tiebreak?  to serve.") },
        confirmButton = { TextButton(onClick = confirm ) { Text("Start") } },
        dismissButton = { TextButton(onClick = onBack) { Text("No, continue set") } }
    )
}


@Composable
private fun GameOverDialog(
    winner: String,
    onBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onBack,
        title = { Text("Match over") },
        confirmButton = { TextButton(onClick = onBack) { Text("Exit") } },
        dismissButton = { TextButton(onClick = onBack) { Text("New match") } }
    )
}

@Composable
private fun ScoreBoard(
    onBack: () -> Unit,
    uiState: MatchScreenData,
    incP1: () -> Unit, decP1: () -> Unit,
    incP2: () -> Unit, decP2: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        }
        Text(uiState.sport.name, style = MaterialTheme.typography.headlineMedium)

        PlayerRow(uiState.user1, uiState.p1Display, uiState.p1DisplayGame, uiState.p1DisplaySet, incP1, decP1)
        PlayerRow(uiState.user2, uiState.p2Display, uiState.p2DisplayGame, uiState.p2DisplaySet, incP2, decP2)
    }
}

@Composable
private fun PlayerRow(
    name: String,
    score: String, // Current point e.g. "AD", "30"
    gameScore: String, // Games won in current set e.g. "5"
    gameSet: String, // Sets won e.g. "1"
    onInc: () -> Unit,
    onDec: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Option A: Inline labels with values
        Text("$name", style = MaterialTheme.typography.titleLarge) // Name is already a label
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ScoreDisplayUnit(label = "Set", value = gameSet)
            ScoreDisplayUnit(label = "Game", value = gameScore)
            ScoreDisplayUnit(label = "Point", value = score)
        }
        // IconButton row
        Row {
            IconButton(onClick = onDec) { Icon(Icons.Filled.Remove, null) }
            IconButton(onClick = onInc) { Icon(Icons.Filled.Add, null) }
        }
    }
}

// Helper composable for consistent label-value display
@Composable
fun ScoreDisplayUnit(label: String, value: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall) // Smaller label
        Text(text = value, style = MaterialTheme.typography.titleLarge) // Score value
    }
}

@Composable
fun Names(
    onBack: () -> Unit,
    uiState: MatchScreenData,
    setName1: (String) -> Unit,
    setName2: (String) -> Unit,
    setSet: (Boolean) -> Unit
){
    Box (Modifier.fillMaxSize()) {
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
                value = uiState.user1,
                onValueChange = setName1,
                label = { Text("Player 1 name") },
                singleLine = true
            )

            TextField(
                value = uiState.user2,
                onValueChange = setName2,
                label = { Text("Player 2 name") },
                singleLine = true
            )
            Button(onClick = { setSet(true) }) {
                Text("Start game")
            }
        }
    }

}

@Preview
@Composable
fun app(){
    MatchScreen(onBack = {}, sportType = SportType.PICKLEBALL)
}