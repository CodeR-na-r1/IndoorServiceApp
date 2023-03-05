package com.mrx.indoorserviceapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region

class MainActivity : AppCompatActivity() {

    lateinit var beaconReferenceApplication: BeaconReferenceApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        beaconReferenceApplication = application as BeaconReferenceApplication

        val region: Region = Region("all-beacons-region", null, null, null)
        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(region)

        regionViewModel.rangedBeacons.observe(this, rangingObserver)
        BeaconManager.getInstanceForApplication(this).startRangingBeacons(region)
    }

    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d("myTag", "Ranged: ${beacons.count()} beacons")
    }

    companion object {
        val TAG = "MyTag"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }
}