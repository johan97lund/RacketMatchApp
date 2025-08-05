package com.johan.racketmatchapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.johan.racketmatchapp.ui.components.AppTopBar
import com.johan.racketmatchapp.ui.screen.MainScreen
import com.johan.racketmatchapp.ui.screen.SportSelectionScreen
import com.johan.racketmatchapp.ui.screen.MatchScreen
import com.johan.racketmatchapp.ui.screen.SettingsScreen
import com.johan.racketmatchapp.data.model.SportType

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    // Hämta aktuell rutt för att styra AppTopBar
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        topBar = {
            AppTopBar(
                currentRoute = currentRoute,
                navController = navController
            )
        },
        bottomBar = {

            if (currentRoute != Destinations.Settings.route) {
                Box(
                    Modifier
                        .fillMaxWidth()
                            .padding(16.dp)

                ) {
                    IconButton(
                        onClick = { navController.navigate(Destinations.Settings.route) },
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Inställningar"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Main.route,
            modifier = Modifier.padding(innerPadding)
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
                SportSelectionScreen(
                    onSelect = { chosenSport ->
                        navController.navigate(Destinations.gameRoute(chosenSport))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 3. Match – parametriserad med sportType
            composable(
                route = "${Destinations.Game.baseRoute}/{sportType}",
                arguments = listOf(navArgument("sportType") {
                    type = NavType.StringType
                })
            ) { backStack ->
                val sport = SportType.valueOf(
                    backStack.arguments!!.getString("sportType")!!
                )
                MatchScreen(
                    sportType = sport,
                    onBack    = { navController.popBackStack() }
                )
            }

            // 4. Inställningar
            composable(Destinations.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
