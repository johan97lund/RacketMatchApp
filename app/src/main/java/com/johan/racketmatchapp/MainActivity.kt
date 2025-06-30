package com.johan.racketmatchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.johan.racketmatchapp.ui.navigation.AppNavHost
import com.johan.racketmatchapp.ui.theme.RacketMatchAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RacketMatchAppTheme {
                AppNavHost()
            }
        }
    }
}