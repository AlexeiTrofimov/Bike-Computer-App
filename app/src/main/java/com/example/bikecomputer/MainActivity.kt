package com.example.bikecomputer

import android.annotation.SuppressLint
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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.github.anastr.speedviewlib.TubeSpeedometer
import java.util.*


private const val DEVICE_ADDRESS = "F0:08:D1:65:9C:0E"

class MainActivity : AppCompatActivity() {

    /* SETUP */

    private val speedListener : MutableLiveData<Float> =  MutableLiveData<Float>()
    private val connectionListener : MutableLiveData<Boolean> =  MutableLiveData<Boolean>()

    private val connectionManager = ConnectionManager(speedListener, connectionListener)

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

    private var isConnected = false
        @SuppressLint("SetTextI18n")
        set(value) {
            val connStatusImg = findViewById<ImageView>(R.id.connection_state_img)
            val connStatusText = findViewById<TextView>(R.id.connection_state_text)
            field = value
            if (value){
                connStatusImg.setImageResource(android.R.drawable.presence_online)
                connStatusText.text = "Connected"
            }
            else{
                connStatusImg.setImageResource(android.R.drawable.presence_invisible)
                connStatusText.text = "Disconnected"
            }
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.popup_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.scan_btn -> {
                if (isScanning) {
                    stopBleScan()
                }
                else {
                    if (!isLocationPermissionGranted){
                        requestLocationPermission()
                    }
                    else{
                        startBleScan()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.scan_btn)
        if (isScanning) {
            item.title = "Stop scan"
        } else {
            item.title = "Scan for devices"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val speedometer = findViewById<TubeSpeedometer>(R.id.speedometer)

        speedListener.value = 0F

        speedListener.observe(this,{
            speedometer.speedTo(it,4000)
        })

        connectionListener.observe(this,{ isConnected = it})


        findViewById<Button>(R.id.testBtn).setOnClickListener {
            connectionManager.enableNotifications()
        }
    }
}