package com.johan.racketmatchapp.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface BlueToothInterface {
    val isAdapterEnabled : StateFlow<Boolean>
    val isScanning : StateFlow<Boolean>
    val foundDevices : StateFlow<List<BluetoothDevice>>

    fun requestEnableBluetoothIntent(): Any
    fun getRequiredPermissions(): List<String>

    fun startSearch()
    fun stopSearch()
    fun release()
}

class BlueToothController(  val context: Context

)  : BlueToothInterface {
    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()


    private val _foundDevices: StateFlow<List<BluetoothDevice>>
        get() {
            TODO()
        }
    override val foundDevices: StateFlow<List<BluetoothDevice>>
        get() {
            TODO()
        }
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val _isAdapterEnabled = MutableStateFlow(bluetoothAdapter?.isEnabled == true)
    override val isAdapterEnabled: StateFlow<Boolean>
        get() = _isAdapterEnabled.asStateFlow()

    override fun getRequiredPermissions(): List<String> {
        return listOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }



    override fun requestEnableBluetoothIntent(): Any {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            return enableBtIntent
        }else{
            return false
        }
    }

    override fun startSearch() {
        TODO("Not yet implemented")
    }

    override fun stopSearch() {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }




}