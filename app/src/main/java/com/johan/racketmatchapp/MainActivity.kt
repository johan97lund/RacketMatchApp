package com.johan.racketmatchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.core.data.local.daos.MatchDao
import com.johan.racketmatchapp.core.data.local.daos.PlayerDao
import com.johan.racketmatchapp.core.settings.SettingsViewModel
import com.johan.racketmatchapp.ui.navigation.AppNavHost
import com.johan.racketmatchapp.ui.theme.RacketMatchAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main entry point of the application.
 *
 * Sets up the Compose content, applies the app theme based on the user's dark mode preference,
 * and initializes the navigation host. Observes the UI state from [SettingsViewModel] to
 * dynamically update the theme.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var playerDao: PlayerDao
    @Inject lateinit var matchDao: MatchDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            playerDao.getAllPlayers()
            matchDao.getAllMatches()
        }

        setContent {
            val settingsVM: SettingsViewModel = viewModel()
            val uiState by settingsVM.uiState.collectAsState()

            RacketMatchAppTheme(darkTheme = uiState.darkMode) {
                Surface {
                    AppNavHost()
                }
            }
        }
    }
}