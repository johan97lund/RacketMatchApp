package com.johan.racketmatchapp.settings.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
/**
 * Handles user preference storage and retrieval using Jetpack DataStore.
 *
 * Provides reactive flows for dark mode and language preferences, and exposes
 * suspend functions to update these preferences. Preferences are persisted in
 * a private DataStore named "settings_prefs".
 *
 * @param context The application context used to access DataStore.
 */
private val Context.dataStore by preferencesDataStore("settings_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }
    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    val languageFlow: Flow<String> = context.dataStore.data
        .map { prefs -> (prefs[LANGUAGE_KEY] ?: "sv") as String }   //default på Svenska

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
    suspend fun setLanguage(code: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = code
        }
    }
}