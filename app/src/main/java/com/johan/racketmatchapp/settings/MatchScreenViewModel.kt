package com.johan.racketmatchapp.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.data.model.SportType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    override var p1Display: String = "0"
        private set
    override var p2Display: String = "0"
        private set

    private fun won() = (p1 >= target || p2 >= target) && kotlin.math.abs(p1 - p2) >= winBy

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



sealed interface GameEvent {
    data class GameOver(val winner: Int)      : GameEvent
    data class TieBreak(val firstServer: Int) : GameEvent
    data class Advantage(val player: Int)     : GameEvent
    data class Score(val player: Int)         : GameEvent
    data class UndoScore(val player: Int)     : GameEvent

}




data class MatchScreenData(
    val user1: String,
    val user2: String,
    val sport: SportType,
    val p1Display: String = "0",
    val p2Display: String = "0",
    val namesSet: Boolean
)

class MatchScreenViewModel(
    private val initialSport: SportType
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MatchScreenData(
            user1 = "",
            user2 = "",
            sport = initialSport,
            p1Display = "0",
            p2Display = "0",
            namesSet = false
        )
    )



    private val _events1 = MutableSharedFlow<GameEvent>()

    val events1: SharedFlow<GameEvent> = _events1.asSharedFlow()

    private val engine = GenericEngine(
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

    )

    val uiState: StateFlow<MatchScreenData> = _uiState.asStateFlow()

    fun setUser1(name: String) = _uiState.update { it.copy(user1 = name) }
    fun setUser2(name: String) = _uiState.update { it.copy(user2 = name) }

    fun setNamesSet(value: Boolean) = _uiState.update { it.copy(namesSet = value) }

    fun incP1() = applyScore { engine.scoreP1() }
    fun incP2() = applyScore { engine.scoreP2() }
    fun decP1() = applyScore { engine.undoP1() }
    fun decP2() = applyScore { engine.undoP2() }

    private fun winnerName() =
        if (engine.p1Display.toInt() > engine.p2Display.toInt())
            _uiState.value.user1
        else
            _uiState.value.user2

    private inline fun applyScore(block: () -> Any) {
        val event = block()
        _uiState.update {
            it.copy(p1Display = engine.p1Display, p2Display = engine.p2Display)
        }

        viewModelScope.launch { _events1.emit(event as GameEvent) }


    }



}

class MatchScreenVmFactory(private val sport: SportType) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MatchScreenViewModel(sport) as T
}