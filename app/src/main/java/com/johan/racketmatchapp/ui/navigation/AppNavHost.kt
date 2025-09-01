package com.johan.racketmatchapp.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.johan.racketmatchapp.core.data.model.SportType
import com.johan.racketmatchapp.ui.components.AppTopBaren
import com.johan.racketmatchapp.ui.screen.BluetoothMatchScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Hämta aktuell rutt för att styra AppTopBar
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
            ) {
                if (currentRoute != Destinations.Main.route) {
                    val titleText = when (currentRoute) {
                        Destinations.SportSelection.route -> "Select Sport"
                        Destinations.Game.route -> "Game"
                        Destinations.Settings.route -> "Settings"
                        else -> ""
                    }
                    AppTopBaren(
                        onBack = { navController.popBackStack() },
                        title = titleText,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    )
                }
            }
        },

        bottomBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)

            ) {
                if (currentRoute != Destinations.Settings.route) {
                    IconButton(
                        onClick = { navController.navigate(Destinations.Settings.route) },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
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
                    }
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
                    onBack    = { navController.popBackStack() },
                    navBluetooth = {navController.navigate(Destinations.blueToothGameRoute(sport))},
                    snackbarHostState = snackbarHostState
                )
            }

            // 4. Inställningar
            composable(Destinations.Settings.route) {
                SettingsScreen()
            }

            composable(
                route = "${Destinations.Game.baseRoute}bluetooth/{sportType}",
                arguments = listOf(navArgument("sportType") {
                    type = NavType.StringType
                })
            ) { backStack ->
                val sport = SportType.valueOf(
                    backStack.arguments!!.getString("sportType")!!
                )
                BluetoothMatchScreen(
                    sportType = sport,
                    onBack    = { navController.popBackStack() }
                )
            }

        }
    }
}
