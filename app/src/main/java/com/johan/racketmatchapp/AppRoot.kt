package com.johan.racketmatchapp

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.ui.navigation.AppNavHost
import com.johan.racketmatchapp.ui.screen.PermissionGate
import com.johan.racketmatchapp.ui.theme.RacketMatchAppTheme
import com.johan.racketmatchapp.ui.viewmodel.SettingsViewModel

@Composable
fun AppRoot() {
    val settingsVM: SettingsViewModel = viewModel()
    val uiState by settingsVM.uiState.collectAsState()

    var gatePassed by remember { mutableStateOf(false) }

    RacketMatchAppTheme(darkTheme = uiState.darkMode) {
        Surface {
            if (gatePassed) {
                AppNavHost()
            } else {
                PermissionGate(onReady = { gatePassed = true })
            }
        }
    }
}
