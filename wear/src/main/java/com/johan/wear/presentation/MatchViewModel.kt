package com.johan.wear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PadelScore {
    LOVE, FIFTEEN, THIRTY, FORTY, ADVANTAGE
}

data class PlayerState(
    var score: PadelScore = PadelScore.LOVE,
    var gameScore: Int = 0,
    var setScore: Int = 0,
    var TieBreakScore: Int = 0
)

sealed interface GameEvent {
    data class GameOver(val player: Int) : GameEvent
    data class TieBreak(val player: Int) : GameEvent
    data class Advantage(val player: Int) : GameEvent
    data class Score(val player: Int) : GameEvent
    data class UndoScore(val player: Int) : GameEvent
    data class Deuce(val player: Int) : GameEvent
    data class PaddelGameWon(val player: Int) : GameEvent
    data class SetScore(val player: Int) : GameEvent
}

class PaddelEngine(private val setLimit: Int = 5) {
    private val players = listOf(PlayerState(), PlayerState())
    var tieBreak: Boolean = false
    var inDeuce: Boolean = false

    fun getp1DisplayScore(): String = if (tieBreak) players[0].TieBreakScore.toString() else padelScoreToString(players[0].score)
    fun getp2DisplayScore(): String = if (tieBreak) players[1].TieBreakScore.toString() else padelScoreToString(players[1].score)
    fun get1DisplayGame(): String = players[0].gameScore.toString()
    fun get2DisplayGame(): String = players[1].gameScore.toString()
    fun get1DisplaySet(): String = players[0].setScore.toString()
    fun get2DisplaySet(): String = players[1].setScore.toString()

    fun setTieBreakTrue() { tieBreak = true }

    private fun padelScoreToString(score: PadelScore): String = when (score) {
        PadelScore.LOVE -> "0"
        PadelScore.FIFTEEN -> "15"
        PadelScore.THIRTY -> "30"
        PadelScore.FORTY -> "40"
        PadelScore.ADVANTAGE -> "ADV"
    }

    fun increaseScore(scoringPlayer: Boolean): GameEvent = if (tieBreak) tiebreakThingy(scoringPlayer) else calcScore(scoringPlayer)

    private fun tiebreakThingy(isP1: Boolean): GameEvent {
        val idx = if (isP1) 0 else 1
        val oppIdx = if (isP1) 1 else 0
        players[idx].TieBreakScore++
        if (players[idx].TieBreakScore >= 7 && (players[idx].TieBreakScore - players[oppIdx].TieBreakScore) >= 2) {
            players[idx].setScore++
            return if (players[idx].setScore >= (setLimit / 2) + 1) {
                resetAfterSetWin()
                GameEvent.GameOver(idx)
            } else {
                resetAfterSetWin()
                GameEvent.SetScore(idx)
            }
        }
        return GameEvent.Score(idx)
    }

    private fun calcScore(isP1: Boolean): GameEvent {
        val idx = if (isP1) 0 else 1
        val oppIdx = if (isP1) 1 else 0
        if (inDeuce) {
            if (players[idx].score == PadelScore.FORTY) {
                if (players[oppIdx].score == PadelScore.ADVANTAGE) {
                    players[oppIdx].score = PadelScore.FORTY
                    players[idx].score = PadelScore.FORTY
                    return GameEvent.Score(idx)
                } else {
                    players[idx].score = PadelScore.ADVANTAGE
                    return GameEvent.Score(idx)
                }
            } else {
                return gameWin(idx, oppIdx)
            }
        } else {
            val next = simpleIncrease(players[idx].score)
            if (next == PadelScore.FORTY && players[oppIdx].score == PadelScore.FORTY) {
                players[idx].score = PadelScore.FORTY
                inDeuce = true
                return GameEvent.Deuce(idx)
            } else if (players[idx].score == PadelScore.FORTY) {
                return gameWin(idx, oppIdx)
            } else {
                players[idx].score = next
                return GameEvent.Score(idx)
            }
        }
    }

    private fun gameWin(idx: Int, oppIdx: Int): GameEvent {
        players[idx].gameScore++
        if (players[idx].gameScore >= 6 && (players[idx].gameScore - players[oppIdx].gameScore) >= 2) {
            players[idx].setScore++
            resetAfterSetWin()
            if (players[idx].setScore >= (setLimit / 2) + 1) return GameEvent.GameOver(idx)
        } else if (players[idx].gameScore == 6 && players[oppIdx].gameScore == 6) {
            resetAfterGameWin()
            tieBreak = true
            return GameEvent.TieBreak(idx)
        }
        resetAfterGameWin()
        return GameEvent.PaddelGameWon(idx)
    }

    private fun resetAfterGameWin() {
        players.forEach { it.score = PadelScore.LOVE; it.TieBreakScore = 0 }
        inDeuce = false
    }

    private fun resetAfterSetWin() {
        players.forEach { it.score = PadelScore.LOVE; it.gameScore = 0; it.TieBreakScore = 0 }
        tieBreak = false
    }

    private fun simpleIncrease(score: PadelScore): PadelScore = when (score) {
        PadelScore.LOVE -> PadelScore.FIFTEEN
        PadelScore.FIFTEEN -> PadelScore.THIRTY
        PadelScore.THIRTY -> PadelScore.FORTY
        else -> PadelScore.ADVANTAGE
    }
}

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
    private var engine = PaddelEngine(setLimit = 3)
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
            engine = PaddelEngine(setLimit = 3)
            scoreHistory.forEach { engine.increaseScore(it) }
            updateUI()
            viewModelScope.launch { _events.emit(GameEvent.UndoScore(0)) }
        }
    }

    private fun applyScore(block: () -> GameEvent) {
        val event = block()
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