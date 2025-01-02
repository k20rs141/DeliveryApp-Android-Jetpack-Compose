package com.example.deliveryapp.model

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.BatteryManager
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.R
import com.example.deliveryapp.data.LocationData
import com.example.deliveryapp.network.OhtomiApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service(), SensorEventListener {
    @Inject
    lateinit var ohtomiRepository: OhtomiRepository
    @Inject
    lateinit var deviceInfoRepository: DeviceInfoRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var currentAcceleration = AccelerationData(0f, 0f, 0f)
    private var previousLatitude: Double = 0.0
    private var previousLongitude: Double = 0.0
    private var previousTimeMillis: Long = 0

    private var carId: Int = 0

    data class AccelerationData(
        val x: Float,
        val y: Float,
        val z: Float
    )

    companion object {
        const val CHANNEL_ID = "location_channel"
        const val CHANNEL_NAME = "Location Service Channel"
        const val NOTIFICATION_ID = 1
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.lastLocation?.let { location ->
                getLocationData(location)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        startForegroundServiceWithNotification()
        startLocationUpdates()
        startAccelerometerUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    private fun startForegroundServiceWithNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(R.string.app_name.toString())
            .setContentText("位置情報を取得中")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startLocationUpdates() {
        val locationInterval: Long = 3000 // 30秒ごとに更新
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationInterval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            Log.e("LocationService", "Location permission not granted")
            stopSelf()
        }
    }

    private fun getLocationData(location: Location) {
        val date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val localTime: String = format.format(date).toString()

        val hourlySpeed = location.speed * 3.6
        val currentTimeMillis = System.currentTimeMillis()

        val distance = HubenyDistance.calcDistance(
            previousLatitude,
            previousLongitude,
            location.latitude,
            location.longitude
        )

        val timeMillsGap = currentTimeMillis - previousTimeMillis
        val timeGap = timeMillsGap / 1000
        val calculatedSpeed = (distance / timeGap) * 3.6

        CoroutineScope(Dispatchers.IO).launch {
            val deviceInfo = deviceInfoRepository.getDeviceInfo()
            deviceInfo?.carId?.let {
                carId = it
            }
        }

        val locationData = LocationData(
            heartRate = 0,
            lat = location.latitude,
            lon = location.longitude,
            carId = carId,
            speed = hourlySpeed.toInt(),
            distance = distance.toInt(),
            timeGap = timeGap.toInt(),
            bearing = location.bearing.toInt(),
            calculatedSpeed = calculatedSpeed.toInt(),
            userAccelerationX = currentAcceleration.x.toInt(),
            userAccelerationY = currentAcceleration.y.toInt(),
            userAccelerationZ = currentAcceleration.z.toInt(),
            battery = getBattery(),
            localTime = localTime
        )

        CoroutineScope(Dispatchers.IO).launch {
            val result = ohtomiRepository.sendLocationData(locationData)
            result.onSuccess { data ->
                previousLatitude = locationData.lat
                previousLongitude = locationData.lon
                previousTimeMillis = currentTimeMillis
                Log.d("LocationService", "Location: ${locationData}")
            }.onFailure { error ->

            }
        }
    }

    private fun startAccelerometerUpdates() {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun getBattery(): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            this.registerReceiver(null, ifilter)
        }

        val batteryPct = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level / scale.toFloat() * 100
        }

        return batteryPct!!.toInt()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            currentAcceleration = AccelerationData(
                x = event.values[0],
                y = event.values[1],
                z = event.values[2]
            )
        }
    }

    // センサーの精度が変更された時の処理
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("LocationService", "Sensor accuracy changed: $accuracy")
    }
}