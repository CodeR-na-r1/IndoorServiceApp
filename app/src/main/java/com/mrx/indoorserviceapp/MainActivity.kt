package com.mrx.indoorserviceapp

/* После запуска права приложению нужно выдать вручную в настройки -> приложения */

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mrx.indoorservice.api.IndoorService
import com.mrx.indoorservice.domain.model.BeaconsEnvironmentInfo
import com.mrx.indoorservice.domain.model.EnvironmentInfo
import com.mrx.indoorservice.domain.model.Point
import com.mrx.indoorservice.domain.model.StateEnvironment
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.service.ArmaRssiFilter
import org.altbeacon.beacon.service.RunningAverageRssiFilter

class MainActivity : AppCompatActivity() {

    private val beaconReferenceApplication: BeaconReferenceApplication by lazy { application as BeaconReferenceApplication }
    private val indoorService: IndoorService by lazy { IndoorService.getInstance(this) }
    private val textBeacons :TextView by lazy { findViewById(R.id.textViewBeacons) }
    private val textPosition :TextView by lazy { findViewById(R.id.textViewPosition) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        indoorService.Position.setEnvironment(stateEnv)

        //BeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter::class.java)
        //RunningAverageRssiFilter.setSampleExpirationMilliseconds(5000L)

        BeaconManager.setRssiFilterImplClass(ArmaRssiFilter::class.java);

        indoorService.BeaconsEnvironment.getRangingViewModel().observe(this, observer)
        indoorService.BeaconsEnvironment.startRanging()
    }

    private val observer = Observer<Collection<BeaconsEnvironmentInfo>>{ beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beaconsы")

        //if (beacons.size < 4)
            //return@Observer

        var temp = "Ranged: ${beacons.count()} beacons\n"
        for (beacon in beacons.sortedBy { it.beaconId }) {
            Log.d(TAG, "${beacon.beaconId} --> ${beacon.distance}")
            temp += "${beacon.beaconId} --> ${beacon.distance}\n"
        }
        textBeacons.text = temp

        if (beacons.size > 1) {
            val envInfo = beacons.map { EnvironmentInfo(it.beaconId, it.distance) }

            val posInfo = indoorService.Position.getPosition(envInfo)
            textPosition.text = "(${posInfo.position.x}, ${posInfo.position.y})"
        }
    }

    private val stateEnv = listOf(StateEnvironment("DF:6A:59:AE:F9:CC", Point<Double>(0.0, 0.0)),
        StateEnvironment("D3:81:75:66:79:B8", Point<Double>(2.0, 0.0)),
        StateEnvironment("E4:C1:3F:EF:49:D7", Point<Double>(2.0, 1.4)),
        StateEnvironment("E6:96:DA:5C:82:59", Point<Double>(2.0, 1.4)))

    companion object {
        const val TAG = "myTag"
    }
}