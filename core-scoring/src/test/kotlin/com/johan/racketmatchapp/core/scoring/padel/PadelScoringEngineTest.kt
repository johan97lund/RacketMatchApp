package com.johan.racketmatchapp.core.scoring.padel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PadelScoringEngineTest {

    @Test
    fun `tiebreak prompt appears at six all`() {
        val engine = PadelScoringEngine(MatchConfig(setLimit = 3))

        repeat(5) {
            winGame(engine, Team.P1)
            winGame(engine, Team.P2)
        }

        winGame(engine, Team.P1)
        val result = winGame(engine, Team.P2)

        assertTrue(result.events.contains(ScoringEvent.ShowTiebreakPrompt))
        assertEquals(true, result.state.needsTiebreakDecision)
    }

    @Test
    fun `deuce advantage and game resolution`() {
        val engine = PadelScoringEngine(MatchConfig(setLimit = 3))

        repeat(3) { engine.apply(ScoringAction.PointWon(Team.P1)) }
        repeat(2) { engine.apply(ScoringAction.PointWon(Team.P2)) }
        val deuceResult = engine.apply(ScoringAction.PointWon(Team.P2))
        assertTrue(deuceResult.events.contains(ScoringEvent.EnteredDeuce))

        val advantage = engine.apply(ScoringAction.PointWon(Team.P1))
        assertTrue(advantage.events.contains(ScoringEvent.EnteredAdvantage(Team.P1)))

        val backToDeuce = engine.apply(ScoringAction.PointWon(Team.P2))
        assertTrue(backToDeuce.events.contains(ScoringEvent.EnteredDeuce))

        val advantageP2 = engine.apply(ScoringAction.PointWon(Team.P2))
        assertTrue(advantageP2.events.contains(ScoringEvent.EnteredAdvantage(Team.P2)))

        val gameWon = engine.apply(ScoringAction.PointWon(Team.P2))
        assertTrue(gameWon.events.contains(ScoringEvent.GameWon(Team.P2)))
    }

    @Test
    fun `set win requires two game lead`() {
        val engine = PadelScoringEngine(MatchConfig(setLimit = 3))

        repeat(5) { winGame(engine, Team.P1) }
        repeat(5) { winGame(engine, Team.P2) }

        winGame(engine, Team.P1)
        val noSetWin = engine.state.sets.valueFor(Team.P1)
        assertEquals(0, noSetWin)

        winGame(engine, Team.P1)
        assertEquals(1, engine.state.sets.valueFor(Team.P1))
    }

    @Test
    fun `tiebreak win requires two point lead`() {
        val engine = PadelScoringEngine(MatchConfig(setLimit = 3))

        reachSixAll(engine)
        engine.apply(ScoringAction.TiebreakDecision(start = true))

        repeat(6) { engine.apply(ScoringAction.PointWon(Team.P1)) }
        repeat(6) { engine.apply(ScoringAction.PointWon(Team.P2)) }
        engine.apply(ScoringAction.PointWon(Team.P1))
        assertEquals(0, engine.state.sets.valueFor(Team.P1))

        engine.apply(ScoringAction.PointWon(Team.P1))
        assertEquals(1, engine.state.sets.valueFor(Team.P1))
    }

    @Test
    fun `regression - watch tiebreak prompt emits before starting tiebreak`() {
        val engine = PadelScoringEngine(MatchConfig(setLimit = 3))

        reachSixAll(engine)
        assertEquals(true, engine.state.needsTiebreakDecision)
        assertEquals(MatchMode.REGULAR, engine.state.mode)
    }

    private fun winGame(engine: PadelScoringEngine, team: Team): ScoringResult {
        repeat(3) { engine.apply(ScoringAction.PointWon(team)) }
        return engine.apply(ScoringAction.PointWon(team))
    }

    private fun reachSixAll(engine: PadelScoringEngine) {
        repeat(5) {
            winGame(engine, Team.P1)
            winGame(engine, Team.P2)
        }
        winGame(engine, Team.P1)
        winGame(engine, Team.P2)
    }
}
