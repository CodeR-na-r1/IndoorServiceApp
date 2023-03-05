package com.mrx.indoorserviceapp

/* После запуска права приложению нужно выдать вручную в настройки -> приложения */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.mrx.indoorservice.api.IndoorService
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region

class MainActivity : AppCompatActivity() {

    lateinit var beaconReferenceApplication: BeaconReferenceApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        beaconReferenceApplication = application as BeaconReferenceApplication

        val indoorService = IndoorService.getInstance(this)

        indoorService.BeaconsEnvironment.getRangingViewModel().observe(this) { beacons ->
            Log.d("myTag", "Ranged: ${beacons.count()} beacons")
        }

        indoorService.BeaconsEnvironment.startRanging()
    }

    companion object {
        val TAG = "MyTag"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }
}