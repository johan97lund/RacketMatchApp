package com.johan.racketmatchapp.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
    private var tieBreak: Boolean = false;
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
enum class PadelScore {
    LOVE, FIFTEEN, THIRTY, FORTY, ADVANTAGE
}
data class PlayerState(
    var score: PadelScore = PadelScore.LOVE,
    var gameScore: Int = 0,
    var setScore: Int = 0,
    var TieBreakScore: Int = 0
)

data class MatchHistory(
    var history : List<GameEvent>
)
class PaddelEngine(private val setLimit : Int = 5){

    private val players = listOf(PlayerState(), PlayerState())

    var tieBreak : Boolean = false;
    var tieBreakSet : Boolean = false;
    var inDeuce : Boolean = false;

    fun getp1DisplayScore(): String {
        if (tieBreak){
            return players[0].TieBreakScore.toString()
        }else{
            return padelScoreToString(players[0].score)
        }
    }
    fun getp2DisplayScore(): String {
        if (tieBreak){
            return players[1].TieBreakScore.toString()
        }else{
            return padelScoreToString(players[1].score)
        }
    }

    fun get1DisplayGame() : String {
        return players[0].gameScore.toString()
    }
    fun get2DisplayGame() : String{
        return players[1].gameScore.toString()
    }
    fun get1DisplaySet() : String{
        return players[0].setScore.toString()
    }
    fun get2DisplaySet() : String{
        return players[1].setScore.toString()
    }

    fun setTieBreakTrue(){
        tieBreak = true;
    }


    fun padelScoreToString(score: PadelScore): String {
        return when (score){
            PadelScore.LOVE    -> "LOVE"
            PadelScore.FIFTEEN -> "FIFTEEN"
            PadelScore.THIRTY  -> "THIRTY"
            PadelScore.FORTY  -> "FORTY"
            PadelScore.ADVANTAGE -> "ADVANTAGE"
        }
    }


    //entry point increase score player
    fun increaseScore(scoringPlayer: Boolean) : GameEvent{
        if (tieBreak){
            return tiebreakThingy(scoringPlayer)
        }else{
            return calcScore(scoringPlayer)
        }

    }

    private fun tiebreakThingy(scoringPlayer: Boolean) : GameEvent{
        val scoringPlayerIndex = if (scoringPlayer) 0 else 1
        val opponentPlayerIndex = if (scoringPlayer) 1 else 0

        val scoringPlayer = players[scoringPlayerIndex]
        val opponentPlayer = players[opponentPlayerIndex]

        scoringPlayer.TieBreakScore++
        if (scoringPlayer.TieBreakScore >= 7 && (scoringPlayer.TieBreakScore - opponentPlayer.TieBreakScore) >= 2){
            scoringPlayer.setScore++
            if (scoringPlayer.setScore >= (setLimit / 2) + 1){
                resetAfterSetWin()
                return GameEvent.GameOver(scoringPlayerIndex);
            }else{
                resetAfterSetWin()
                return GameEvent.SetScore(scoringPlayerIndex)
            }
        }else{
            return GameEvent.Score(scoringPlayerIndex)
        }

    }

    private fun calcScore(scoringPlayer: Boolean): GameEvent {
        // om scoringPlayer == false -> scoring player == 2
        // om scoringPlayer == true -> scoring player == 1
        val scoringPlayerIndex = if (scoringPlayer) 0 else 1
        val opponentPlayerIndex = if (scoringPlayer) 1 else 0

        val scoringPlayer = players[scoringPlayerIndex]
        val opponentPlayer = players[opponentPlayerIndex]
        if (inDeuce){
            if (scoringPlayer.score == PadelScore.FORTY){
                if (opponentPlayer.score == PadelScore.ADVANTAGE){
                    opponentPlayer.score = PadelScore.FORTY
                    scoringPlayer.score = simpleIncrease(scoringPlayer.score) // du får advantage
                    return GameEvent.Score(scoringPlayerIndex)
                }else //opponent score = FORTY
                {
                    scoringPlayer.score = simpleIncrease(scoringPlayer.score)
                    return GameEvent.Score(scoringPlayerIndex)
                }
            }else{
                //du vann DEUCE
                return gameWin(scoringPlayer, opponentPlayer, scoringPlayerIndex, opponentPlayerIndex)
            }
        }else{ // normal scoring
            val TryScore : PadelScore = simpleIncrease(scoringPlayer.score)
            if (triggerDeuce(TryScore, opponentPlayer)){
                scoringPlayer.score = simpleIncrease(scoringPlayer.score)
                inDeuce = true
                return GameEvent.Deuce(scoringPlayerIndex)
            }else{// om inte trigger deuce
                // om vann "gameet"
                if (TryScore == PadelScore.ADVANTAGE && opponentPlayer.score != PadelScore.FORTY){
                    //öka game score
                    //kolla om tiebreak
                    //kolla om vann settet
                    return gameWin(scoringPlayer, opponentPlayer, scoringPlayerIndex, opponentPlayerIndex)
                }else{
                    scoringPlayer.score = TryScore;
                    return GameEvent.Score(scoringPlayerIndex)
                }
            }
        }

    }

    fun gameWin(scoringPlayer : PlayerState, opponentPlayer: PlayerState, scoringPlayerIndex : Int, opponentPlayerIndex : Int) : GameEvent
    {
        scoringPlayer.gameScore++
        if (scoringPlayer.gameScore >= 6 && (scoringPlayer.gameScore - opponentPlayer.gameScore) >= 2){
            scoringPlayer.setScore++
            resetAfterSetWin();
            //kolla om vann hela matchen
            if (scoringPlayer.setScore >= (setLimit / 2) + 1){
                return GameEvent.GameOver(scoringPlayerIndex);
            }
            //kolla om de blir TieBreak
        }else if (scoringPlayer.gameScore == 6 && opponentPlayer.gameScore == 6){
            players.forEach { it ->
                it.score = PadelScore.LOVE
                it.TieBreakScore = 0
            }
            inDeuce = false
            return GameEvent.TieBreak(scoringPlayerIndex);
        }
        resetAfterGameWin()
        return GameEvent.PaddelGameWon(scoringPlayerIndex)

    }
    fun resetAfterGameWin(){
        players.forEach { it ->
            it.score = PadelScore.LOVE
            it.TieBreakScore = 0
        }
        inDeuce = false
    }

    fun resetAfterSetWin(){
        players.forEach { it ->
            it.score = PadelScore.LOVE
            it.gameScore = 0;
            it.TieBreakScore = 0;
        }
        tieBreak = false;
    }



    //
    fun triggerDeuce(newScore: PadelScore, opponentPlayer : PlayerState): Boolean = newScore == PadelScore.FORTY && opponentPlayer.score == PadelScore.FORTY




    private fun isDeuce(): Boolean {
        if (players[0].score == PadelScore.FORTY && players[1].score == PadelScore.FORTY){
            return true;
        }else{
            return false
        }
    }


    fun simpleIncrease(score : PadelScore) : PadelScore{
        return when (score){
            PadelScore.LOVE    -> PadelScore.FIFTEEN
            PadelScore.FIFTEEN -> PadelScore.THIRTY
            PadelScore.THIRTY  -> PadelScore.FORTY
            PadelScore.FORTY  -> PadelScore.ADVANTAGE
            PadelScore.ADVANTAGE -> PadelScore.ADVANTAGE
        }
    }
}



sealed interface GameEvent {
    data class GameOver(
        val player: Int

    )      : GameEvent
    data class TieBreak(val player: Int) : GameEvent
    data class Advantage(val player: Int)     : GameEvent
    data class Score(val player: Int)         : GameEvent
    data class UndoScore(val player: Int)     : GameEvent
    data class Deuce(val player: Int)         : GameEvent

    data class PaddelGameWon(val player: Int) : GameEvent

    data class SetScore(val player: Int) : GameEvent

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
)




class MatchScreenViewModel(
    private val initialSport: SportType
) : ViewModel() {
    private val engine = PaddelEngine(

        setLimit = 3
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
            namesSet = false
        )
    )



    private val _events1 = MutableSharedFlow<GameEvent>()

    val events1: SharedFlow<GameEvent> = _events1.asSharedFlow()


    val uiState: StateFlow<MatchScreenData> = _uiState.asStateFlow()

    fun setUser1(name: String) = _uiState.update { it.copy(user1 = name)}
    fun setUser2(name: String) = _uiState.update { it.copy(user2 = name)}

    fun setNamesSet(value: Boolean) = _uiState.update { it.copy(namesSet = value) }

    fun incP1() = applyScore { engine.increaseScore(true) }
    fun incP2() = applyScore { engine.increaseScore(false) }
    fun decP1() = applyScore { TODO() }
    fun decP2() = applyScore { TODO() }

    fun startTieBreak(){
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
    private inline fun applyScore(block: () -> Any) {
        val event = block()
        updateUI()

        viewModelScope.launch { _events1.emit(event as GameEvent) }
    }
    private fun updateUI(){
        _uiState.update {
            it.copy(
                p1Display = engine.getp1DisplayScore(),
                p2Display = engine.getp2DisplayScore(),
                p1DisplayGame = engine.get1DisplayGame(),
                p2DisplayGame = engine.get2DisplayGame(),
                p1DisplaySet = engine.get1DisplaySet(),
                p2DisplaySet = engine.get2DisplaySet(),
            )
        }
    }
}


class MatchScreenVmFactory(private val sport: SportType) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MatchScreenViewModel(sport) as T
}