package com.example.bikecomputer

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

private const val DEVICE_ADDRESS = "F0:08:D1:65:9C:0E"
private var returned: String = ""

class MainActivity : AppCompatActivity() {

    /* SETUP */
    private val connectionManager = ConnectionManager()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBtPermission.launch(enableBtIntent)
        }
    }

    private val isLocationPermissionGranted
        get() = hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        requestPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

   private val requestPermission =  registerForActivityResult(
       ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) startBleScan()
        else requestLocationPermission()
    }

    private val requestBtPermission =  registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                promptEnableBluetooth()
            }
    }

    /* BLE SCAN */

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()


    private fun startBleScan() {
        val filters: MutableList<ScanFilter> = ArrayList()
        val filter = ScanFilter.Builder().setDeviceAddress(DEVICE_ADDRESS).build()
        filters.add(filter)
        bleScanner.startScan(filters, scanSettings, scanCallback)
        isScanning = true
    }


    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (isScanning) {
                stopBleScan()
            }
            with(result.device) {
                Log.i("Connection Status","Connecting to $address")
                connectGatt(this@MainActivity.applicationContext, false, connectionManager.gattCallback)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("ScanCallback", "onScanFailed: code $errorCode")
        }
    }

    fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    private var isScanning = false
        set(value) {
            field = value
            runOnUiThread { findViewById<Button>(R.id.scan_button).text = if (value) "Stop Scan" else "Start Scan" }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.scan_button).setOnClickListener {
            if (isScanning) {
                stopBleScan()
            } else {
                if (!isLocationPermissionGranted){
                    requestLocationPermission()
                }
                else{
                    startBleScan()
                }
            }
        }
        findViewById<Button>(R.id.testBtn).setOnClickListener {
            connectionManager.receiveData()
            findViewById<TextView>(R.id.returnText).text = returned
        }
    }
}