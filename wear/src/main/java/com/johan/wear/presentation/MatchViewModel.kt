package com.johan.wear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.racketmatchapp.core.scoring.padel.GameEvent
import com.johan.racketmatchapp.core.scoring.padel.PadelEngine
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
    private var engine = PadelEngine(setLimit = 3)
    private val scoreHistory = mutableListOf<Boolean>() // true = P1, false = P2
    
    private val _uiState = MutableStateFlow(MatchUiState())
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>()
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    fun scoreP1() {
        scoreHistory.add(true)
        applyScore { engine.increaseScore(true) }
    }

    fun scoreP2() {
        scoreHistory.add(false)
        applyScore { engine.increaseScore(false) }
    }

    fun undo() {
        if (scoreHistory.isNotEmpty()) {
            scoreHistory.removeAt(scoreHistory.size - 1)
            // Re-calculate state from scratch
            engine = PadelEngine(setLimit = 3)
            scoreHistory.forEach { scoredByP1 ->
                val event = engine.increaseScore(scoredByP1)
                if (event is GameEvent.TieBreak) {
                    engine.setTieBreakTrue()
                }
            }
            updateUI()
            viewModelScope.launch { _events.emit(GameEvent.UndoScore(0)) }
        }
    }

    private fun applyScore(block: () -> GameEvent) {
        val event = block()
        if (event is GameEvent.TieBreak) {
            engine.setTieBreakTrue()
        }
        updateUI()
        viewModelScope.launch { _events.emit(event) }
    }

    private fun updateUI() {
        _uiState.update {
            it.copy(
                p1Score = engine.getp1DisplayScore(),
                p2Score = engine.getp2DisplayScore(),
                p1Games = engine.get1DisplayGame(),
                p2Games = engine.get2DisplayGame(),
                p1Sets = engine.get1DisplaySet(),
                p2Sets = engine.get2DisplaySet(),
                canUndo = scoreHistory.isNotEmpty()
            )
        }
    }
}
