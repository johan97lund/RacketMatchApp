package com.johan.racketmatchapp.core.scoring.padel

sealed interface ScoringAction {
    data class PointWon(val team: Team) : ScoringAction
    data class TiebreakDecision(val start: Boolean) : ScoringAction
}

sealed interface ScoringEvent {
    data class PointScored(val team: Team) : ScoringEvent
    data class UndoPoint(val team: Team) : ScoringEvent
    data object EnteredDeuce : ScoringEvent
    data class EnteredAdvantage(val team: Team) : ScoringEvent
    data class GameWon(val team: Team) : ScoringEvent
    data class SetWon(val team: Team) : ScoringEvent
    data class MatchWon(val team: Team) : ScoringEvent
    data object ShowTiebreakPrompt : ScoringEvent
    data object TiebreakStarted : ScoringEvent
}

data class ScoringResult(
    val state: MatchState,
    val events: List<ScoringEvent> = emptyList()
)

class PadelScoringEngine(config: MatchConfig = MatchConfig()) {
    private val setsToWin = config.setsToWin

    var state: MatchState = MatchState(setsToWin = setsToWin)
        private set

    fun apply(action: ScoringAction): ScoringResult {
        val result = reduce(state, action)
        state = result.state
        return result
    }

    private fun reduce(state: MatchState, action: ScoringAction): ScoringResult =
        when (action) {
            is ScoringAction.PointWon -> applyPoint(state, action.team)
            is ScoringAction.TiebreakDecision -> applyTiebreakDecision(state, action.start)
        }

    private fun applyTiebreakDecision(state: MatchState, start: Boolean): ScoringResult {
        if (!state.needsTiebreakDecision) {
            return ScoringResult(state)
        }
        return if (start) {
            val newState = state.copy(
                mode = MatchMode.TIEBREAK,
                needsTiebreakDecision = false,
                tieBreakPoints = Score(),
                points = PointScore(),
                inDeuce = false
            )
            ScoringResult(newState, listOf(ScoringEvent.TiebreakStarted))
        } else {
            val newState = state.copy(
                mode = MatchMode.REGULAR,
                needsTiebreakDecision = false,
                games = Score(),
                points = PointScore(),
                tieBreakPoints = Score(),
                inDeuce = false
            )
            ScoringResult(newState)
        }
    }

    private fun applyPoint(state: MatchState, team: Team): ScoringResult {
        if (state.needsTiebreakDecision) {
            return ScoringResult(state)
        }
        return if (state.mode == MatchMode.TIEBREAK) {
            applyTieBreakPoint(state, team)
        } else {
            applyRegularPoint(state, team)
        }
    }

    private fun applyTieBreakPoint(state: MatchState, team: Team): ScoringResult {
        val updatedTieBreak = state.tieBreakPoints.increment(team)
        val teamScore = updatedTieBreak.valueFor(team)
        val opponentScore = updatedTieBreak.valueFor(team.opponent())
        if (teamScore >= 7 && (teamScore - opponentScore) >= 2) {
            return applySetWin(state.copy(tieBreakPoints = updatedTieBreak), team)
        }
        return ScoringResult(
            state.copy(tieBreakPoints = updatedTieBreak),
            listOf(ScoringEvent.PointScored(team))
        )
    }

    private fun applyRegularPoint(state: MatchState, team: Team): ScoringResult {
        if (state.inDeuce) {
            return applyDeucePoint(state, team)
        }

        val current = state.points.valueFor(team)
        val opponent = state.points.valueFor(team.opponent())
        val next = current.next()

        return when {
            next == GamePoint.FORTY && opponent == GamePoint.FORTY -> {
                val newState = state.copy(
                    points = state.points
                        .withValue(team, GamePoint.FORTY)
                        .withValue(team.opponent(), GamePoint.FORTY),
                    inDeuce = true
                )
                ScoringResult(newState, listOf(ScoringEvent.EnteredDeuce))
            }
            current == GamePoint.FORTY -> {
                applyGameWin(state, team)
            }
            else -> {
                val newState = state.copy(points = state.points.withValue(team, next))
                ScoringResult(newState, listOf(ScoringEvent.PointScored(team)))
            }
        }
    }

    private fun applyDeucePoint(state: MatchState, team: Team): ScoringResult {
        val teamPoint = state.points.valueFor(team)
        val opponentPoint = state.points.valueFor(team.opponent())
        return when {
            teamPoint == GamePoint.ADVANTAGE -> applyGameWin(state, team)
            opponentPoint == GamePoint.ADVANTAGE -> {
                val newState = state.copy(
                    points = PointScore(GamePoint.FORTY, GamePoint.FORTY),
                    inDeuce = true
                )
                ScoringResult(newState, listOf(ScoringEvent.EnteredDeuce))
            }
            else -> {
                val newState = state.copy(
                    points = state.points
                        .withValue(team, GamePoint.ADVANTAGE)
                        .withValue(team.opponent(), GamePoint.FORTY),
                    inDeuce = true
                )
                ScoringResult(newState, listOf(ScoringEvent.EnteredAdvantage(team)))
            }
        }
    }

    private fun applyGameWin(state: MatchState, team: Team): ScoringResult {
        val updatedGames = state.games.increment(team)
        val teamGames = updatedGames.valueFor(team)
        val opponentGames = updatedGames.valueFor(team.opponent())
        val baseState = state.copy(
            games = updatedGames,
            points = PointScore(),
            tieBreakPoints = Score(),
            inDeuce = false
        )

        return when {
            teamGames >= 6 && (teamGames - opponentGames) >= 2 -> {
                applySetWin(baseState, team)
            }
            teamGames == 6 && opponentGames == 6 -> {
                val newState = baseState.copy(needsTiebreakDecision = true)
                ScoringResult(
                    newState,
                    listOf(ScoringEvent.GameWon(team), ScoringEvent.ShowTiebreakPrompt)
                )
            }
            else -> ScoringResult(baseState, listOf(ScoringEvent.GameWon(team)))
        }
    }

    private fun applySetWin(state: MatchState, team: Team): ScoringResult {
        val updatedSets = state.sets.increment(team)
        val teamSets = updatedSets.valueFor(team)
        val newState = state.copy(
            sets = updatedSets,
            games = Score(),
            points = PointScore(),
            tieBreakPoints = Score(),
            mode = MatchMode.REGULAR,
            needsTiebreakDecision = false,
            inDeuce = false
        )

        return if (teamSets >= state.setsToWin) {
            ScoringResult(newState, listOf(ScoringEvent.MatchWon(team)))
        } else {
            ScoringResult(newState, listOf(ScoringEvent.SetWon(team)))
        }
    }
}

private fun Team.opponent(): Team = if (this == Team.P1) Team.P2 else Team.P1

private fun GamePoint.next(): GamePoint = when (this) {
    GamePoint.LOVE -> GamePoint.FIFTEEN
    GamePoint.FIFTEEN -> GamePoint.THIRTY
    GamePoint.THIRTY -> GamePoint.FORTY
    GamePoint.FORTY -> GamePoint.ADVANTAGE
    GamePoint.ADVANTAGE -> GamePoint.ADVANTAGE
}
