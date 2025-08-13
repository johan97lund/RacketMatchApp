package com.johan.racketmatchapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.johan.racketmatchapp.core.data.model.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class viewData(
    val searching : Boolean
)

class MatchBluetoothViewModel(
    private val initialSport: SportType
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        viewData(
            searching = true
        )
    )

    val uiState: StateFlow<viewData> = _uiState.asStateFlow()

    fun setSearching(value: Boolean) = _uiState.update { it.copy(searching = value) }



}

class MatchBluetoothVmFactory(private val sport: SportType) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MatchBluetoothViewModel(sport) as T
}