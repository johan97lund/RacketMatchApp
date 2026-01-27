package com.johan.racketmatchapp.core.scoring.padel

enum class Team {
    P1,
    P2
}

enum class GamePoint {
    LOVE,
    FIFTEEN,
    THIRTY,
    FORTY,
    ADVANTAGE
}

data class Score(val p1: Int = 0, val p2: Int = 0) {
    fun increment(team: Team): Score =
        if (team == Team.P1) copy(p1 = p1 + 1) else copy(p2 = p2 + 1)

    fun valueFor(team: Team): Int = if (team == Team.P1) p1 else p2
}

data class PointScore(
    val p1: GamePoint = GamePoint.LOVE,
    val p2: GamePoint = GamePoint.LOVE
) {
    fun valueFor(team: Team): GamePoint = if (team == Team.P1) p1 else p2

    fun withValue(team: Team, point: GamePoint): PointScore =
        if (team == Team.P1) copy(p1 = point) else copy(p2 = point)
}

enum class MatchMode {
    REGULAR,
    TIEBREAK
}

data class MatchConfig(val setLimit: Int = 3) {
    val setsToWin: Int = (setLimit / 2) + 1
}

data class MatchState(
    val setsToWin: Int,
    val sets: Score = Score(),
    val games: Score = Score(),
    val points: PointScore = PointScore(),
    val tieBreakPoints: Score = Score(),
    val mode: MatchMode = MatchMode.REGULAR,
    val inDeuce: Boolean = false,
    val needsTiebreakDecision: Boolean = false
) {
    fun displayPoint(team: Team): String =
        if (mode == MatchMode.TIEBREAK) {
            tieBreakPoints.valueFor(team).toString()
        } else {
            points.valueFor(team).toDisplayString()
        }

    fun displayGames(team: Team): String = games.valueFor(team).toString()
    fun displaySets(team: Team): String = sets.valueFor(team).toString()
}

fun GamePoint.toDisplayString(): String = when (this) {
    GamePoint.LOVE -> "0"
    GamePoint.FIFTEEN -> "15"
    GamePoint.THIRTY -> "30"
    GamePoint.FORTY -> "40"
    GamePoint.ADVANTAGE -> "ADV"
}
