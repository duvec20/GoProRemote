package com.duvec.goproremote.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile

class GoProCamera(context: Context) {

    private val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter
    private val scanner get() = adapter?.bluetoothLeScanner

    private val _status = MutableStateFlow("Not connected")
    val status: StateFlow<String> = _status.asStateFlow()

    private var foundDevice: BluetoothDevice? = null

    private val appContext = context.applicationContext
    private var bluetoothGatt: BluetoothGatt? = null

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            foundDevice = result.device
            _status.value = "Found ${result.device.name ?: "GoPro"}"
            stopScan()
            connect(result.device)
        }

        override fun onScanFailed(errorCode: Int) {
            _status.value = "Scan failed ($errorCode)"
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _status.value = "Reading services…"
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _status.value = "Disconnected"
                gatt.close()
                bluetoothGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val goProService = gatt.getService(GOPRO_SERVICE_UUID)
            _status.value = if (goProService != null) "Connected" else "GoPro service missing"
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        val scanner = scanner
        if (scanner == null) {
            _status.value = "Bluetooth is off"
            return
        }
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(GOPRO_SERVICE_UUID))
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        _status.value = "Searching…"
        scanner.startScan(listOf(filter), settings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanner?.stopScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    private fun connect(device: BluetoothDevice) {
        _status.value = "Connecting…"
        bluetoothGatt = device.connectGatt(appContext, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    companion object {
        val GOPRO_SERVICE_UUID: UUID =
            UUID.fromString("0000fea6-0000-1000-8000-00805f9b34fb")
    }
}