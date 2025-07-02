package com.johan.racketmatchapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.johan.racketmatchapp.ui.screen.MainScreen
import com.johan.racketmatchapp.ui.screen.MatchScreen
import com.johan.racketmatchapp.ui.screen.SettingsScreen

@Preview(
    name = "AppNavHost on Pixel 5 (API 30)",
    device = "id:pixel_5",  // <-- note the “:api30” suffix
    showBackground = true
)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Destinations.Main.route
    ) {
        composable(Destinations.Main.route) {
            MainScreen(
                onMatchClick = { navController.navigate(Destinations.Game.route) },
                onSettingsClick = { navController.navigate(Destinations.Settings.route) }
            )
        }
        composable(Destinations.Game.route) {
            MatchScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}