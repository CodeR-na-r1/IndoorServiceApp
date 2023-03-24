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
import com.mrx.indoorserviceapp.databinding.ActivityMainBinding
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.service.ArmaRssiFilter
import org.altbeacon.beacon.service.RunningAverageRssiFilter

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val beaconManager by lazy { BeaconManager.getInstanceForApplication(this) }

    private val indoorService: IndoorService by lazy { IndoorService.getInstance(this) }

    private val dataBeacons = mutableMapOf<String, Double>()

    private val beaconsPositions = listOf(
        StateEnvironment("E4:C1:3F:EF:49:D7", Point(0.0, 0.0)),
        StateEnvironment("D3:81:75:66:79:B8", Point(7.0, 0.0)),
        StateEnvironment("CF:CA:06:0F:D0:F9", Point(10.0, 7.0)),
        StateEnvironment("E6:96:DA:5C:82:59", Point(0.0, 7.0))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        indoorService.Position.setEnvironment(beaconsPositions)

        //BeaconManager.setRssiFilterImplClass(ArmaRssiFilter::class.java);

        indoorService.BeaconsEnvironment.getRangingViewModel().observe(this, observerBeacons)
        indoorService.AzimuthManager.getAzimuthViewModel().observe(this, observerAzimuth)

        indoorService.BeaconsEnvironment.startRanging()
        indoorService.AzimuthManager.startListen()
    }

    override fun onPause() {
        super.onPause()

        indoorService.BeaconsEnvironment.stopRanging()
        indoorService.AzimuthManager.stopListen()
    }

    private val observerBeacons = Observer<Collection<BeaconsEnvironmentInfo>>{ beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")

        beacons.forEach { beacon ->
            dataBeacons[beacon.beaconId] = beacon.distance
        }

        var textForTextView = "Ranged: ${beacons.size} beacons\n"
        dataBeacons.forEach { beacon ->
            textForTextView += "${beacon.key} -> ${beacon.value}\n"
        }
        binding.textViewBeacons.text = textForTextView

        if (beacons.size > 2) {
            val posInfo = indoorService.Position.getPosition(dataBeacons.map { EnvironmentInfo(it.key, it.value) })
            binding.textViewPosition.text = "Position: (${posInfo.position.x}, ${posInfo.position.y})"
        }
    }

    private val observerAzimuth = Observer<Float> { azimuth ->
        binding.textViewAzimuth.text = azimuth.toInt().toString()
    }

    companion object {
        const val TAG = "myTag"
    }
}