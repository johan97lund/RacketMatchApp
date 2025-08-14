package com.johan.racketmatchapp.ui.screen

import android.widget.Button
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johan.racketmatchapp.core.data.model.SportType
import com.johan.racketmatchapp.ui.viewmodel.GameEvent
import com.johan.racketmatchapp.ui.viewmodel.MatchBluetoothViewModel
import com.johan.racketmatchapp.ui.viewmodel.MatchBluetoothVmFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BluetoothMatchScreen(
    onBack: () -> Unit,
    sportType: SportType
) {
    val vm: MatchBluetoothViewModel = viewModel(
        factory = MatchBluetoothVmFactory(sportType)
    )

    val state by vm.uiState.collectAsState()

    if (!state.searching){
        Testone(setSearching = vm::setSearching)
    }else{
        Testtwo(setSearching = vm::setSearching)
    }

}

@Composable
fun Testone(setSearching : (Boolean) -> Unit){
    Button(onClick = { setSearching(true) }) {
        Text("searching = false")
    }
}

@Composable
fun Testtwo(setSearching : (Boolean) -> Unit){
    Button(onClick = { setSearching(false) }) {
        Text("searching = true")
    }
}