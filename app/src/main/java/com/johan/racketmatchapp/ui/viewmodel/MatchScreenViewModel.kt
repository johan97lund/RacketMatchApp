package com.johan.racketmatchapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.johan.racketmatchapp.core.scoring.padel.GameEvent
import com.johan.racketmatchapp.core.scoring.padel.PadelEngine
import com.johan.racketmatchapp.core.data.model.SportType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

interface ScoreInterface {
    val p1Display: String
    val p2Display: String

    fun scoreP1(): GameEvent
    fun scoreP2(): GameEvent

    fun undoP1(): GameEvent

    fun undoP2(): GameEvent
}

class GenericEngine(
    private val target: Int = 5,
    private val winBy: Int = 2,
    private val step: Int = 1
) : ScoreInterface {

    private var p1 = 0
    private var p2 = 0
    private var tieBreak: Boolean = false;
    override var p1Display: String = "0"
        private set
    override var p2Display: String = "0"
        private set






    private fun won() = (p1 >= target || p2 >= target) && abs(p1 - p2) >= winBy

    override fun  undoP1() = if (p1 > 0) { p1 -= step; p1Display = p1.toString() ; GameEvent.UndoScore(1)
    } else GameEvent.UndoScore(1)
    override fun undoP2() = if (p2 > 0) { p2 -= step; p2Display = p2.toString() ; GameEvent.UndoScore(2) } else GameEvent.UndoScore(2)
    override fun scoreP1(): GameEvent {
        p1 += step
        p1Display = p1.toString()
        if (won()){
            return GameEvent.GameOver(1);
        }
        return GameEvent.Score(1);
    }

    override fun scoreP2(): GameEvent {
        p2 += step
        p2Display = p2.toString()
        if (won()){
            return GameEvent.GameOver(2);
        }
        return GameEvent.Score(2);
    }
}
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
sealed interface ScoreAction {
    data class Score(val isP1: Boolean) : ScoreAction
    data object StartTieBreak : ScoreAction
}





class MatchScreenViewModel(
    private val initialSport: SportType
) : ViewModel() {
    private val setLimit = 3
    private var engine = PadelEngine(
        setLimit = setLimit
        /*
        target = when (initialSport){
            SportType.PADEL -> 15
            SportType.TENNIS -> 40
            else -> 5
        },
        winBy = when (initialSport){
            SportType.TENNIS -> 30
            else -> 2
        },
        step = when (initialSport){
            SportType.TENNIS -> 15
            else -> 1
        }

         */

    )
    private val scoreHistory = mutableListOf<ScoreAction>()
    private val _uiState = MutableStateFlow(
        MatchScreenData(
            user1 = "",
            user2 = "",
            sport = initialSport,
            p1Display = engine.getp1DisplayScore(),
            p2Display = engine.getp2DisplayScore(),
            p1DisplayGame = engine.get1DisplayGame(),
            p2DisplayGame = engine.get2DisplayGame(),
            p1DisplaySet = engine.get1DisplaySet(),
            p2DisplaySet = engine.get2DisplaySet(),
            namesSet = false,
            canUndoP1 = false,
            canUndoP2 = false,

        )
    )



    private val _events1 = MutableSharedFlow<GameEvent>()

    val events1: SharedFlow<GameEvent> = _events1.asSharedFlow()


    val uiState: StateFlow<MatchScreenData> = _uiState.asStateFlow()

    fun setUser1(name: String) = _uiState.update { it.copy(user1 = name)}
    fun setUser2(name: String) = _uiState.update { it.copy(user2 = name)}

    fun setNamesSet(value: Boolean) = _uiState.update { it.copy(namesSet = value) }


    fun incP1() {
        scoreHistory.add(ScoreAction.Score(isP1 = true))
        applyScore { engine.increaseScore(true) }
    }

    fun incP2() {
        scoreHistory.add(ScoreAction.Score(isP1 = false))
        applyScore { engine.increaseScore(false) }
    }

    fun decP1() = undoLastScore(isP1 = true)
    fun decP2() = undoLastScore(isP1 = false)

    fun startTieBreak(){
        scoreHistory.add(ScoreAction.StartTieBreak)
        engine.setTieBreakTrue()
        updateUI()
    }

    /*
    private fun winnerName() =
        if (engine.p1Display.toInt() > engine.p2Display.toInt())
            _uiState.value.user1
        else
            _uiState.value.user2
*/
    private inline fun applyScore(block: () -> GameEvent) {
        val event = block()
        updateUI()

        viewModelScope.launch { _events1.emit(event) }
    }

    private fun undoLastScore(isP1: Boolean) {
        val lastAction = scoreHistory.lastOrNull() as? ScoreAction.Score
        if (lastAction?.isP1 != isP1) return
        scoreHistory.removeLast()
        rebuildEngineFromHistory()
        updateUI()
        viewModelScope.launch { _events1.emit(GameEvent.UndoScore(if (isP1) 0 else 1)) }
    }

    private fun rebuildEngineFromHistory() {
        val rebuilt = PadelEngine(setLimit = setLimit)
        scoreHistory.forEach { action ->
            when (action) {
                is ScoreAction.Score -> rebuilt.increaseScore(action.isP1)
                ScoreAction.StartTieBreak -> rebuilt.setTieBreakTrue()
            }
        }
        engine = rebuilt    }

    private fun updateUI(){
        val lastScore = scoreHistory.lastOrNull() as? ScoreAction.Score
        _uiState.update {
            it.copy(
                p1Display = engine.getp1DisplayScore(),
                p2Display = engine.getp2DisplayScore(),
                p1DisplayGame = engine.get1DisplayGame(),
                p2DisplayGame = engine.get2DisplayGame(),
                p1DisplaySet = engine.get1DisplaySet(),
                p2DisplaySet = engine.get2DisplaySet(),
                canUndoP1 = lastScore?.isP1 == true,
                canUndoP2 = lastScore?.isP1 == false
            )
        }
    }
}


class MatchScreenVmFactory(private val sport: SportType) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MatchScreenViewModel(sport) as T
}
