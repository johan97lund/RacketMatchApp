package com.johan.racketmatchapp.ui.screen

import android.content.Context
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
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.core.data.model.SportType
import com.johan.racketmatchapp.ui.viewmodel.GameEvent
import com.johan.racketmatchapp.ui.viewmodel.MatchScreenData
import com.johan.racketmatchapp.ui.viewmodel.MatchScreenViewModel
import com.johan.racketmatchapp.ui.viewmodel.MatchScreenVmFactory
import com.johan.racketmatchapp.wear.WearSync
import kotlinx.coroutines.flow.collectLatest


/**
 * Match screen with one big combined scoreboard (p1–p2).
 */

@Composable
fun MatchScreen(
    onBack: () -> Unit,
    sportType: SportType,
    navBluetooth: () -> Unit,
    snackbarHostState : SnackbarHostState
) {
    val vm: MatchScreenViewModel = viewModel(
        factory = MatchScreenVmFactory(sportType)
    )
    val state by vm.uiState.collectAsState()
    val tieBreak = remember { mutableStateOf(false) }
    val gameOver = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(24.dp)
    ) {
        if (!state.namesSet) {
            Names(
                onBack   = onBack,
                uiState  = state,
                setName1 = vm::setUser1,
                setName2 = vm::setUser2,
                setSet = vm::setNamesSet,
                navBlue = navBluetooth
            )
        } else {
            ScoreBoard(
                uiState = state,
                incP1 = vm::incP1, decP1 = vm::decP1,
                incP2 = vm::incP2, decP2 = vm::decP2
            )
        }
    }

    LaunchedEffect(state.namesSet) {
        if (state.namesSet) {
            val result = try {
                com.johan.racketmatchapp.wear.WearSync(context).sendStart()
            } catch (t: Throwable) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    message = "Failed to start sync to Wear OS: ${t.localizedMessage}",
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

    if (tieBreak.value) {
        TieBreakDialog(
            onBack = onBack,
            confirm = {
                vm.startTieBreak()
                tieBreak.value = false
            },
            tieBreakState = tieBreak
        )
    }

    if (gameOver.value){
        val p1Sets = state.p1DisplaySet.toIntOrNull() ?: 0
        val p2Sets = state.p2DisplaySet.toIntOrNull() ?: 0
        val winner = if (p1Sets >= p2Sets) state.user1 else state.user2
        GameOverDialog(winner, onBack)    }
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
        text = { Text("$winner won the match.") },
        confirmButton = { TextButton(onClick = onBack) { Text("Exit") } },
        dismissButton = { TextButton(onClick = onBack) { Text("New match") } }
    )
}

/* ----------------------- NY LAYOUT: EN STOR POÄNGTAVLA ----------------------- */

private enum class Side { Left, Right }

/** Stor central tavla: visar p1Display – p2Display (t.ex. 15–15, 40–30, Adv–40) */
@Composable
private fun CombinedScoreboard(
    p1Display: String,
    p2Display: String
) {
    Text(
        text = "$p1Display – $p2Display",
        style = MaterialTheme.typography.displayLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
private fun ScoreBoard(
    uiState: MatchScreenData,
    incP1: () -> Unit, decP1: () -> Unit,
    incP2: () -> Unit, decP2: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(uiState.sport.name, style = MaterialTheme.typography.headlineMedium)

        // >>> ENDAST EN STOR POÄNGTAVLA I MITTEN <<<
        CombinedScoreboard(
            p1Display = uiState.p1Display,
            p2Display = uiState.p2Display
        )

        // Två kolumner: P1 vänster, P2 höger (utan "Point" här)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            PlayerSide(
                name = uiState.user1,
                gameScore = uiState.p1DisplayGame,
                gameSet = uiState.p1DisplaySet,
                onInc = incP1,
                onDec = decP1,
                canUndo = uiState.canUndoP1,
                modifier = Modifier.weight(1f),
                side = Side.Left
            )
            PlayerSide(
                name = uiState.user2,
                gameScore = uiState.p2DisplayGame,
                gameSet = uiState.p2DisplaySet,
                onInc = incP2,
                onDec = decP2,
                canUndo = uiState.canUndoP2,
                modifier = Modifier.weight(1f),
                side = Side.Right
            )
        }
    }
}

@Composable
private fun PlayerSide(
    name: String,
    gameScore: String,  // Games won in current set e.g. "5"
    gameSet: String,    // Sets won e.g. "1"
    onInc: () -> Unit,
    onDec: () -> Unit,
    canUndo: Boolean,
    modifier: Modifier = Modifier,
    side: Side
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Namn
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Endast Set & Game här (Point tas bort – den visas i den stora tavlan)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreDisplayUnit(label = "Set", value = gameSet)
            ScoreDisplayUnit(label = "Game", value = gameScore)
        }

        // – / +
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledIconButton(
                onClick = onDec,
                enabled = canUndo,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Minus")
            }
            FilledIconButton(
                onClick = onInc,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Plus")
            }
        }
    }
}

/* ----------------------------------------------------------------------------- */

@Composable
fun ScoreDisplayUnit(label: String, value: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(text = value, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun Names(
    onBack: () -> Unit,
    uiState: MatchScreenData,
    setName1: (String) -> Unit,
    setName2: (String) -> Unit,
    setSet: (Boolean) -> Unit,
    navBlue: () -> Unit
){
    Box (Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = uiState.user1,
                onValueChange = setName1,
                label = { Text("Player 1 name") },
                singleLine = true
            )
            TextField(
                value = uiState.user2,
                onValueChange = setName2,
                label = { Text("Player 2 name") },
                singleLine = true
            )
            Button(onClick = { setSet(true) }) {
                Text("Start game")
            }
            Button(onClick = navBlue) {
                Text("find players")
            }
        }
    }
}
