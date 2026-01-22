package com.johan.racketmatchapp.core.scoring.padel

import org.junit.Assert.assertEquals
import org.junit.Test

class PadelEngineTest {

    @Test
    fun `scores progress from love to game`() {
        val engine = PadelEngine(setLimit = 3)

        assertEquals(GameEvent.Score(0), engine.increaseScore(true))
        assertEquals(GameEvent.Score(0), engine.increaseScore(true))
        assertEquals(GameEvent.Score(0), engine.increaseScore(true))
        assertEquals(GameEvent.PaddelGameWon(0), engine.increaseScore(true))
    }

    @Test
    fun `deuce advantage and game resolution`() {
        val engine = PadelEngine(setLimit = 3)

        repeat(3) { engine.increaseScore(true) }
        repeat(2) { engine.increaseScore(false) }
        assertEquals(GameEvent.Deuce(1), engine.increaseScore(false))

        assertEquals(GameEvent.Advantage(0), engine.increaseScore(true))
        assertEquals(GameEvent.Deuce(1), engine.increaseScore(false))
        assertEquals(GameEvent.Advantage(1), engine.increaseScore(false))
        assertEquals(GameEvent.PaddelGameWon(1), engine.increaseScore(false))
    }

    @Test
    fun `tie break triggers at six all`() {
        val engine = PadelEngine(setLimit = 3)

        repeat(5) {
            winGame(engine, isP1 = true)
            winGame(engine, isP1 = false)
        }

        winGame(engine, isP1 = true)
        val event = winGame(engine, isP1 = false)

        assertEquals(GameEvent.TieBreak(1), event)
        assertEquals(false, engine.tieBreak)
    }

    @Test
    fun `tie break scoring wins the set`() {
        val engine = PadelEngine(setLimit = 3)

        repeat(6) {
            winGame(engine, isP1 = true)
            winGame(engine, isP1 = false)
        }

        engine.setTieBreakTrue()
        repeat(7) { engine.increaseScore(true) }

        assertEquals("1", engine.get1DisplaySet())
        assertEquals("0", engine.get2DisplaySet())
    }

    private fun winGame(engine: PadelEngine, isP1: Boolean): GameEvent {
        var event = GameEvent.Score(if (isP1) 0 else 1)
        repeat(4) {
            event = engine.increaseScore(isP1)
        }
        return event
    }
}
