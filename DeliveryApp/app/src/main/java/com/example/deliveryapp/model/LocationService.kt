package com.example.deliveryapp.model

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.BatteryManager
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.deliveryapp.R
import com.example.deliveryapp.data.DefaultAppContainer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService: Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var deviceIdentifier: String = ""
    private var carId: Int = 0

    companion object {
        const val CHANNEL_ID = "location_channel"
        const val CHANNEL_NAME = "Location Service Channel"
        const val NOTIFICATION_ID = 1

        const val ACTION_UPDATE_CAR_ID = "com.example.deliveryapp.ACTION_UPDATE_CAR_ID"
        const val EXTRA_CAR_ID = "extra_car_id"
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val location: Location? = result.lastLocation
            location?.let {
//                getLocationData(it)
                Log.d("LocationService", "Location: ${location.latitude}, ${location.longitude}, ${location.speed}, ${location.bearing}")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startLocationUpdates()
        getDeviceIdentifier()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceWithNotification()
        startLocationUpdates()
        intent?.let {
            if (it.action == ACTION_UPDATE_CAR_ID) {
                val newCarId = it.getIntExtra(EXTRA_CAR_ID, -1)
                if (newCarId != -1) {
                    Log.d("LocationService", "新しいCarIdを受信: $newCarId")
                    fetchCarId(deviceIdentifier, newCarId)
                } else {
                    Log.e("LocationService", "無効なCarIdが受信されました")
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
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

    fun getDeviceIdentifier() {
        val sharedPreferences = applicationContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val key = "android_id"
        // SharedPreferences に保存されている場合はそれを返す
        val savedAndroidId = sharedPreferences.getString(key, null)
        // 初回取得時に ANDROID_ID を取得して保存
        val currentAndroidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if (savedAndroidId == null || savedAndroidId != currentAndroidId) {
            sharedPreferences.edit().putString(key, currentAndroidId).apply()
            deviceIdentifier = currentAndroidId
            fetchCarId(deviceIdentifier = currentAndroidId, carId = carId)
            Log.d("LocationService", "ANDROID_ID: $currentAndroidId")
        } else {
            deviceIdentifier = savedAndroidId
            Log.d("LocationService", "ANDROID_ID:saved: $savedAndroidId")
        }
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
        // SharedPreferences から carId を取得
        val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val androidId = prefs.getString("android_id", "") ?: ""

        val date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val localTime: String = format.format(date).toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = DefaultAppContainer().ohtomiRepository.getLocationData(
                    heartRate = 0,
                    lat = location.latitude,
                    lon = location.longitude,
                    carId = carId,
                    speed = location.speed.toInt(),
                    distance = 0,
                    timeGap = 0,
                    bearing = location.bearing.toInt(),
                    calculatedSpeed = 0,
                    userAccelerationX = 0,
                    userAccelerationY = 0,
                    userAccelerationZ = 0,
                    battery = getBattery(),
                    localTime = localTime
                )

                if (response.isSuccessful) { // ocs_insert.phpのレスポンスをJson形式に変えてくれると...
                    val responseBody = response.body()?.string()
                    Log.d("LocationService", "LocationData APIレスポンス: $responseBody")
                }
            } catch (e: Exception) {
                Log.e("LocationService", "LocationData APIリクエスト失敗: ${e.message}")
            }
        }
    }

    fun fetchCarId(deviceIdentifier: String, carId: Int) {
        val repository = DefaultAppContainer().ohtomiRepository

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val carId = repository.getDeviceData(imie = deviceIdentifier, carId = carId)
                Log.d("LocationService", "取得したCarId: $carId")

                // ViewModelに通知するためBroadcastを送信
                val intent = Intent("CAR_ID_UPDATED").apply {
                    putExtra("car_id", carId)
                }
                sendBroadcast(intent)
            } catch (e: Exception) {
                Log.e("LocationService", "APIリクエスト失敗: ${e.message}")
            }
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
}