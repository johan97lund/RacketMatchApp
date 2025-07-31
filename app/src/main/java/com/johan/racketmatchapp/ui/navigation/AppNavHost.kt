package com.johan.racketmatchapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.johan.racketmatchapp.data.model.SportType
import com.johan.racketmatchapp.ui.screen.MainScreen
import com.johan.racketmatchapp.ui.screen.MatchScreen
import com.johan.racketmatchapp.ui.screen.SettingsScreen
import com.johan.racketmatchapp.ui.screen.SportSelectionScreen

/**
 * Composable function that sets up the navigation host for the application.
 *
 * Defines the navigation graph with routes for the main menu, sport selection,
 * match screen (parameterized by sport type), and settings screen. Each route
 * is associated with its respective composable screen.
 *
 * Includes a preview for visualizing the navigation host in Android Studio.
 */
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
        // 1. Main
        composable(Destinations.Main.route) {
            MainScreen(
                onMatchClick    = { navController.navigate(Destinations.SportSelection.route) },
                onSettingsClick = { navController.navigate(Destinations.Settings.route) }
            )
        }

        // 2. Sportval
        composable(Destinations.SportSelection.route) {
            SportSelectionScreen ( onSelect = { chosenSport ->
                // Vid val, navigera vidare till parametriserad match-rutt
                navController.navigate(Destinations.gameRoute(chosenSport))
            }, onBack = { navController.popBackStack() }

            )
        }

        // 3. Match – parametriserad med sportType
        composable(
            route = "${Destinations.Game.baseRoute}/{sportType}",
            arguments = listOf(navArgument("sportType") { type = NavType.StringType })
        ) { backStackEntry ->
            val sport = SportType.valueOf(backStackEntry.arguments!!.getString("sportType")!!)
            MatchScreen(
                sportType = sport,
                onBack    = { navController.popBackStack() }
            )
        }

        // 4. Inställningar
        composable(Destinations.Settings.route) {
            SettingsScreen(
                onBack = {  navController.popBackStack() }
            )
        }
    }
}
