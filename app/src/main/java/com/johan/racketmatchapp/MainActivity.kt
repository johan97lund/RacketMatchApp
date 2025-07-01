package com.johan.racketmatchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.settings.SettingsViewModel
import com.johan.racketmatchapp.ui.navigation.AppNavHost
import com.johan.racketmatchapp.ui.theme.RacketMatchAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingsViewModel = viewModel()
            val isDark = viewModel.uiState.collectAsState().value.darkMode
            RacketMatchAppTheme(darkTheme = isDark) {
                AppNavHost()
            }
        }
    }
}