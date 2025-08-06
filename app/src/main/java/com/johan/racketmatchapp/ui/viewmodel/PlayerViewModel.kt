package com.johan.racketmatchapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johan.racketmatchapp.core.data.local.daos.PlayerDao
import com.johan.racketmatchapp.core.data.local.entities.PlayerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerDao: PlayerDao
) : ViewModel() {

    val players: StateFlow<List<PlayerEntity>> =
        playerDao
            .getAllPlayers()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    fun addPlayer(username: String) = viewModelScope.launch {
        if (username.isNotBlank()) {
            playerDao.insert(PlayerEntity(username = username))
        }
    }

    fun updatePlayer(player: PlayerEntity) = viewModelScope.launch {
        playerDao.update(player)
    }

    fun removePlayer(player: PlayerEntity) = viewModelScope.launch {
        playerDao.delete(player)
    }
}
