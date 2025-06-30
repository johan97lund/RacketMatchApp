package com.johan.racketmatchapp.ui.navigation

sealed class Destinations (val route: String) {
    object Main : Destinations("main")
    object Game : Destinations("game")
    object Settings : Destinations("settings")

}