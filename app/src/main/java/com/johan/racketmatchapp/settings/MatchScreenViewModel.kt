package com.johan.racketmatchapp.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.data.model.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MatchScreenData(
    val user1: String,
    val user2: String,
    val sport: SportType
)

class MatchScreenViewModel(
    private val initial: SportType
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MatchScreenData(
            user1 = "",
            user2 = "",
            sport = initial
        )
    )

    val uiState: StateFlow<MatchScreenData> = _uiState.asStateFlow()

    fun setUser1(name: String) = _uiState.update { it.copy(user1 = name) }
    fun setUser2(name: String) = _uiState.update { it.copy(user2 = name) }




}

class MatchScreenVmFactory(private val sport: SportType) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MatchScreenViewModel(sport) as T
}