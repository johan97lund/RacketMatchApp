package com.johan.racketmatchapp.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.johan.racketmatchapp.settings.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage(val displayName: String) {
    EN("English"),
    SV("Svenska")
}
data class UiState(
    val darkMode: Boolean = false,
    val language: AppLanguage = AppLanguage.EN
)
/**
 * ViewModel for managing the settings screen's state and user preferences.
 *
 * Provides UI state for dark mode and language preferences, and exposes
 * methods to update these preferences. The preferences are stored and
 * retrieved using the [UserPreferences] class.
 *
 * @param app The [Application] instance used to initialize the ViewModel.
 */
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = UserPreferences(app.applicationContext)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                prefs.darkModeFlow,
                prefs.languageFlow
            ) { dark, lang -> UiState(dark, lang) }
                .collect { _uiState.value = it }
        }
    }



    fun setDarkMode(on: Boolean) = viewModelScope.launch {
        prefs.setDarkMode(on)
    }

    fun setLanguage(lang: AppLanguage) = viewModelScope.launch {
        prefs.setLanguage(lang)
    }
}

