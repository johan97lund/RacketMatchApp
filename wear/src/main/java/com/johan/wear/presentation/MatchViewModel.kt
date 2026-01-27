package com.johan.wear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.racketmatchapp.core.scoring.padel.MatchConfig
import com.johan.racketmatchapp.core.scoring.padel.PadelScoringEngine
import com.johan.racketmatchapp.core.scoring.padel.ScoringAction
import com.johan.racketmatchapp.core.scoring.padel.ScoringEvent
import com.johan.racketmatchapp.core.scoring.padel.Team
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MatchUiState(
    val p1Score: String = "0",
    val p2Score: String = "0",
    val p1Games: String = "0",
    val p2Games: String = "0",
    val p1Sets: String = "0",
    val p2Sets: String = "0",
    val canUndo: Boolean = false
)

class MatchViewModel : ViewModel() {
    private var engine = PadelScoringEngine(MatchConfig(setLimit = 3))
    private val scoreHistory = mutableListOf<ScoringAction>()
    
    private val _uiState = MutableStateFlow(MatchUiState())
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ScoringEvent>()
    val events: SharedFlow<ScoringEvent> = _events.asSharedFlow()

    fun scoreP1() {
        val action = ScoringAction.PointWon(Team.P1)
        scoreHistory.add(action)
        applyAction(action)
    }

    fun scoreP2() {
        val action = ScoringAction.PointWon(Team.P2)
        scoreHistory.add(action)
        applyAction(action)
    }

    fun decideTieBreak(start: Boolean) {
        val action = ScoringAction.TiebreakDecision(start)
        scoreHistory.add(action)
        applyAction(action)
    }

    fun undo() {
        if (scoreHistory.isNotEmpty()) {
            val removed = scoreHistory.removeAt(scoreHistory.size - 1)
            // Re-calculate state from scratch
            engine = PadelScoringEngine(MatchConfig(setLimit = 3))
            scoreHistory.forEach { action ->
                engine.apply(action)
            }
            updateUI()
            if (removed is ScoringAction.PointWon) {
                viewModelScope.launch { _events.emit(ScoringEvent.UndoPoint(removed.team)) }
            }
        }
    }

    private fun applyAction(action: ScoringAction) {
        val result = engine.apply(action)
        updateUI()
        viewModelScope.launch {
            result.events.forEach { event ->
                _events.emit(event)
            }
        }
    }

    private fun updateUI() {
        _uiState.update {
            it.copy(
                p1Score = engine.state.displayPoint(Team.P1),
                p2Score = engine.state.displayPoint(Team.P2),
                p1Games = engine.state.displayGames(Team.P1),
                p2Games = engine.state.displayGames(Team.P2),
                p1Sets = engine.state.displaySets(Team.P1),
                p2Sets = engine.state.displaySets(Team.P2),
                canUndo = scoreHistory.isNotEmpty()
            )
        }
    }
}
