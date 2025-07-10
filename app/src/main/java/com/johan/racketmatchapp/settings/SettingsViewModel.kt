package com.johan.racketmatchapp.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.johan.racketmatchapp.settings.data.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    /**
     * Represents the UI state for the settings screen.
     *
     * @property darkMode Indicates whether dark mode is enabled.
     */
    private val prefs = UserPreferences(app)

    // UI-state som Compose kan läsa
    val uiState = prefs.darkModeFlow
        .map { darkMode -> UiState(darkMode = darkMode) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState())

    val languageState = prefs.languageFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, "sv") // default language is Swedish

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setDarkMode(enabled)
        }
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch {
            prefs.setLanguage(code)
        }
    }

    data class UiState(
        val darkMode: Boolean = false
    )
}