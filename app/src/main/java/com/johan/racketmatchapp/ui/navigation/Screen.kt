package com.johan.racketmatchapp.ui.navigation

import android.net.Uri

sealed class Screen(
    val route: String,
    val title: String,
    val showSettingsAction: Boolean = false
) {
    object Main     : Screen("main",      title = "RacketMatch", showSettingsAction = true)
    object Match    : Screen("match",     title = "Match",       showSettingsAction = true)
    object Settings : Screen("settings",  title = "Inställningar")
    object Padel    : Screen(
        route = "padel_screen/{playerA}/{playerB}",
        title = "Padel"
    ) {
        fun createRoute(playerA: String, playerB: String) =
            "padel_screen/${Uri.encode(playerA)}/${Uri.encode(playerB)}"
    }
}
