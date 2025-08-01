package com.johan.racketmatchapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import com.johan.racketmatchapp.ui.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentRoute: String?,
    navController: NavController
) {
    TopAppBar(
        title = { Text(text = "") },

        // Back-pil på alla rutter utom Main
        navigationIcon = {
            if (currentRoute != Destinations.Main.route) {
                @androidx.compose.runtime.Composable {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tillbaka")
                    }
                }
            } else null
        },

    )
}
