package com.johan.racketmatchapp.core.settings.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.johan.racketmatchapp.core.settings.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    private companion object {
        val DARK_MODE_KEY  = booleanPreferencesKey("dark_mode")
        val LANGUAGE_KEY   = stringPreferencesKey("language")
    }

    /** Emits true/false, default false. */
    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    /** Emits AppLanguage, default Svenska. */
    val languageFlow: Flow<AppLanguage> = context.dataStore.data
        .map { prefs ->
            prefs[LANGUAGE_KEY]
                ?.let { runCatching { AppLanguage.valueOf(it) }.getOrNull() }
                ?: AppLanguage.SV          // default
        }

    /* -------- setters that write to disk -------- */

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    suspend fun setLanguage(lang: AppLanguage) {
        context.dataStore.edit { it[LANGUAGE_KEY] = lang.name }
    }

    suspend fun readDarkModeOnce(): Boolean =
        context.dataStore.data
            .map { it[DARK_MODE_KEY] ?: false }
            .first()
}