package com.johan.racketmatchapp.core.scoring.padel

enum class PadelScore {
    LOVE, FIFTEEN, THIRTY, FORTY, ADVANTAGE
}

data class PlayerState(
    var score: PadelScore = PadelScore.LOVE,
    var gameScore: Int = 0,
    var setScore: Int = 0,
    var tieBreakScore: Int = 0
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

class PadelEngine(private val setLimit: Int = 5) {
    private val players = listOf(PlayerState(), PlayerState())

    var tieBreak: Boolean = false
        private set
    var inDeuce: Boolean = false
        private set

    fun getp1DisplayScore(): String =
        if (tieBreak) players[0].tieBreakScore.toString() else padelScoreToString(players[0].score)

    fun getp2DisplayScore(): String =
        if (tieBreak) players[1].tieBreakScore.toString() else padelScoreToString(players[1].score)

    fun get1DisplayGame(): String = players[0].gameScore.toString()
    fun get2DisplayGame(): String = players[1].gameScore.toString()
    fun get1DisplaySet(): String = players[0].setScore.toString()
    fun get2DisplaySet(): String = players[1].setScore.toString()

    fun setTieBreakTrue() {
        tieBreak = true
    }

    fun increaseScore(scoringPlayer: Boolean): GameEvent =
        if (tieBreak) tiebreakThingy(scoringPlayer) else calcScore(scoringPlayer)

    private fun padelScoreToString(score: PadelScore): String = when (score) {
        PadelScore.LOVE -> "0"
        PadelScore.FIFTEEN -> "15"
        PadelScore.THIRTY -> "30"
        PadelScore.FORTY -> "40"
        PadelScore.ADVANTAGE -> "ADV"
    }

    private fun tiebreakThingy(isP1: Boolean): GameEvent {
        val idx = if (isP1) 0 else 1
        val oppIdx = if (isP1) 1 else 0
        players[idx].tieBreakScore++
        if (players[idx].tieBreakScore >= 7 &&
            (players[idx].tieBreakScore - players[oppIdx].tieBreakScore) >= 2
        ) {
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
                    return GameEvent.Deuce(idx)
                } else {
                    players[idx].score = PadelScore.ADVANTAGE
                    return GameEvent.Advantage(idx)
                }
            }
            return gameWin(idx, oppIdx)
        }

        val next = simpleIncrease(players[idx].score)
        return if (next == PadelScore.FORTY && players[oppIdx].score == PadelScore.FORTY) {
            players[idx].score = PadelScore.FORTY
            inDeuce = true
            GameEvent.Deuce(idx)
        } else if (players[idx].score == PadelScore.FORTY) {
            gameWin(idx, oppIdx)
        } else {
            players[idx].score = next
            GameEvent.Score(idx)
        }
    }

    private fun gameWin(idx: Int, oppIdx: Int): GameEvent {
        players[idx].gameScore++
        if (players[idx].gameScore >= 6 &&
            (players[idx].gameScore - players[oppIdx].gameScore) >= 2
        ) {
            players[idx].setScore++
            resetAfterSetWin()
            if (players[idx].setScore >= (setLimit / 2) + 1) {
                return GameEvent.GameOver(idx)
            }
        } else if (players[idx].gameScore == 6 && players[oppIdx].gameScore == 6) {
            resetAfterGameWin()
            return GameEvent.TieBreak(idx)
        }
        resetAfterGameWin()
        return GameEvent.PaddelGameWon(idx)
    }

    private fun resetAfterGameWin() {
        players.forEach { it.score = PadelScore.LOVE; it.tieBreakScore = 0 }
        inDeuce = false
    }

    private fun resetAfterSetWin() {
        players.forEach {
            it.score = PadelScore.LOVE
            it.gameScore = 0
            it.tieBreakScore = 0
        }
        tieBreak = false
        inDeuce = false
    }

    private fun simpleIncrease(score: PadelScore): PadelScore = when (score) {
        PadelScore.LOVE -> PadelScore.FIFTEEN
        PadelScore.FIFTEEN -> PadelScore.THIRTY
        PadelScore.THIRTY -> PadelScore.FORTY
        else -> PadelScore.ADVANTAGE
    }
}
