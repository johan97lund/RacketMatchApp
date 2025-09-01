package com.johan.racketmatchapp.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.wrappers.Wrappers.packageManager
import com.johan.racketmatchapp.core.bluetooth.BlueToothController
import com.johan.racketmatchapp.core.data.model.SportType
import com.johan.racketmatchapp.ui.viewmodel.GameEvent
import com.johan.racketmatchapp.ui.viewmodel.MatchBluetoothViewModel
import com.johan.racketmatchapp.ui.viewmodel.MatchBluetoothVmFactory
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("RememberReturnType")
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun BluetoothMatchScreen(
    onBack: () -> Unit,
    sportType: SportType
) {
    val context = LocalContext.current
    val blueToothController = BlueToothController(context)
    val state = rememberMultiplePermissionsState(blueToothController.getRequiredPermissions())

    val visaLåda = remember { mutableStateOf(false) }
    if (visaLåda.value){
        AlertDialog(
            onDismissRequest = {
                visaLåda.value = false
            },
            title = {
                Text(
                    text = "Permission",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "The notification is important for this app. Please grant the permission.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        visaLåda.value = false
                        for (permission in state.permissions) {
                            val intent = Intent(
                                permission.permission,
                                Uri.fromParts("package", context.packageName, null)
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(context, intent, null)
                        }

                    }) {
                    Text("OK", style = TextStyle(color = Color.Black))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        visaLåda.value = false
                    }) {
                    Text("Cancel", style = TextStyle(color = Color.Black))
                }
            },
        )

    }







}


@SuppressLint("RememberReturnType")
@Composable
fun test(){


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
