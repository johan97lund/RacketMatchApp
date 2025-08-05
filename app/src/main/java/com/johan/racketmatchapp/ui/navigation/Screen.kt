package com.johan.racketmatchapp.ui.navigation

sealed class Screen(
    val route: String,
    val title: String,
    val showSettingsAction: Boolean = false
) {
    object Main    : Screen("main",    title = "RacketMatch", showSettingsAction = true)
    object Match   : Screen("match",   title = "Match",       showSettingsAction = true)
    object Settings: Screen("settings",title = "Inställningar")

}