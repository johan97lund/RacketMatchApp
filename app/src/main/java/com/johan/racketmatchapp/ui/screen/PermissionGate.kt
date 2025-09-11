package com.johan.racketmatchapp.ui.screen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionGate(
    onReady: () -> Unit
) {
    val context = LocalContext.current
    val isAtLeastS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // Runtime permissions required by OS version
    val requiredPermissions = remember {
        if (isAtLeastS) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
                // Add ADVERTISE too if you actively advertise:
                // Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            // On Android 10 and below, location is needed to scan
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val permState: MultiplePermissionsState = rememberMultiplePermissionsState(requiredPermissions)

    val enableBtLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { /* We'll re-check after */ }

    var triedRequest by rememberSaveable { mutableStateOf(false) }

    // Kick off the request immediately on first composition
    LaunchedEffect(Unit) {
        permState.launchMultiplePermissionRequest()
        triedRequest = true
    }

    val allGranted = permState.permissions.all { it.status.isGranted }

    if (allGranted) {
        // If Bluetooth adapter is off, ask to enable it.
        val btManager = ContextCompat.getSystemService(context, BluetoothManager::class.java)
        val adapter: BluetoothAdapter? = btManager?.adapter
        if (adapter?.isEnabled == true) {
            LaunchedEffect("ready") { onReady() }
        } else {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Turn on Bluetooth",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "This app needs Bluetooth to find and connect to nearby devices.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Button(modifier = Modifier.padding(top = 16.dp), onClick = {
                        enableBtLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }) {
                        Text("Enable Bluetooth")
                    }
                }
            }
        }
    } else {
        val permanentlyDenied = permState.permissions.any { p ->
            !p.status.isGranted && !p.status.shouldShowRationale && triedRequest
        }

        Surface(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Allow Bluetooth permissions",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        "We need permission to scan and connect to nearby devices to sync scores in real-time."
                    else
                        "We need location permission to scan for nearby Bluetooth devices on older Android versions.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )

                if (permanentlyDenied) {
                    Button(modifier = Modifier.padding(top = 16.dp), onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    }) { Text("Open Settings") }
                } else {
                    Button(modifier = Modifier.padding(top = 16.dp), onClick = {
                        permState.launchMultiplePermissionRequest()
                        triedRequest = true
                    }) { Text("Grant permissions") }
                }
            }
        }
    }
}
