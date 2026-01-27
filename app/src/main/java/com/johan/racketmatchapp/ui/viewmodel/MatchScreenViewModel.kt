package com.johan.racketmatchapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.johan.racketmatchapp.core.scoring.padel.MatchConfig
import com.johan.racketmatchapp.core.scoring.padel.PadelScoringEngine
import com.johan.racketmatchapp.core.scoring.padel.ScoringAction
import com.johan.racketmatchapp.core.scoring.padel.ScoringEvent
import com.johan.racketmatchapp.core.scoring.padel.Team
import com.johan.racketmatchapp.core.data.model.SportType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MatchScreenData(
    val user1: String,
    val user2: String,
    val sport: SportType,
    val p1Display: String = "0",
    val p2Display: String = "0",
    val namesSet: Boolean,
    val p1DisplayGame: String = "0",
    val p2DisplayGame : String = "0",
    val p1DisplaySet : String = "0",
    val p2DisplaySet : String = "0",
    val canUndoP1: Boolean = false,
    val canUndoP2: Boolean = false,

)
class MatchScreenViewModel(
    private val initialSport: SportType
) : ViewModel() {
    private val setLimit = 3
    private var engine = PadelScoringEngine(MatchConfig(setLimit = setLimit))
    private val scoreHistory = mutableListOf<ScoringAction>()
    private val _uiState = MutableStateFlow(
        MatchScreenData(
            user1 = "",
            user2 = "",
            sport = initialSport,
            p1Display = engine.state.displayPoint(Team.P1),
            p2Display = engine.state.displayPoint(Team.P2),
            p1DisplayGame = engine.state.displayGames(Team.P1),
            p2DisplayGame = engine.state.displayGames(Team.P2),
            p1DisplaySet = engine.state.displaySets(Team.P1),
            p2DisplaySet = engine.state.displaySets(Team.P2),
            namesSet = false,
            canUndoP1 = false,
            canUndoP2 = false,

        )
    )



    private val _events1 = MutableSharedFlow<ScoringEvent>()

    val events1: SharedFlow<ScoringEvent> = _events1.asSharedFlow()


    val uiState: StateFlow<MatchScreenData> = _uiState.asStateFlow()

    fun setUser1(name: String) = _uiState.update { it.copy(user1 = name)}
    fun setUser2(name: String) = _uiState.update { it.copy(user2 = name)}

    fun setNamesSet(value: Boolean) = _uiState.update { it.copy(namesSet = value) }


    fun incP1() {
        val action = ScoringAction.PointWon(Team.P1)
        scoreHistory.add(action)
        applyAction(action)
    }

    fun incP2() {
        val action = ScoringAction.PointWon(Team.P2)
        scoreHistory.add(action)
        applyAction(action)
    }

    fun decP1() = undoLastScore(isP1 = true)
    fun decP2() = undoLastScore(isP1 = false)

    fun decideTieBreak(start: Boolean) {
        val action = ScoringAction.TiebreakDecision(start = start)
        scoreHistory.add(action)
        applyAction(action)
    }

    /*
    private fun winnerName() =
        if (engine.p1Display.toInt() > engine.p2Display.toInt())
            _uiState.value.user1
        else
            _uiState.value.user2
*/
    private fun applyAction(action: ScoringAction) {
        val result = engine.apply(action)
        updateUI()
        viewModelScope.launch {
            result.events.forEach { event ->
                _events1.emit(event)
            }
        }
    }

    private fun undoLastScore(isP1: Boolean) {
        val lastAction = scoreHistory.lastOrNull() as? ScoringAction.PointWon
        val expectedTeam = if (isP1) Team.P1 else Team.P2
        if (lastAction?.team != expectedTeam) return
        scoreHistory.removeLast()
        rebuildEngineFromHistory()
        updateUI()
        viewModelScope.launch { _events1.emit(ScoringEvent.UndoPoint(expectedTeam)) }
    }

    private fun rebuildEngineFromHistory() {
        val rebuilt = PadelScoringEngine(MatchConfig(setLimit = setLimit))
        scoreHistory.forEach { action ->
            rebuilt.apply(action)
        }
        engine = rebuilt
    }

    private fun updateUI(){
        val lastScore = scoreHistory.lastOrNull() as? ScoringAction.PointWon
        _uiState.update {
            it.copy(
                p1Display = engine.state.displayPoint(Team.P1),
                p2Display = engine.state.displayPoint(Team.P2),
                p1DisplayGame = engine.state.displayGames(Team.P1),
                p2DisplayGame = engine.state.displayGames(Team.P2),
                p1DisplaySet = engine.state.displaySets(Team.P1),
                p2DisplaySet = engine.state.displaySets(Team.P2),
                canUndoP1 = lastScore?.team == Team.P1,
                canUndoP2 = lastScore?.team == Team.P2
            )
        }
    }
}


class MatchScreenVmFactory(private val sport: SportType) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MatchScreenViewModel(sport) as T
}
