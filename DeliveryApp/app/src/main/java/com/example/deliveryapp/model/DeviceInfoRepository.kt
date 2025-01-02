package com.example.deliveryapp.model

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class DeviceInfo(
    val deviceId: String,
    val carId: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

interface DeviceInfoRepository {
    suspend fun saveDeviceInfo(deviceInfo: DeviceInfo)
    suspend fun getDeviceInfo(): DeviceInfo?
    suspend fun getDeviceId(): String
}

@Singleton
class DeviceInfoRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
): DeviceInfoRepository {

    private val deviceIdKey = stringPreferencesKey("device_id")
    private val carIdKey = intPreferencesKey("car_id")
    private val timestampKey = longPreferencesKey("timestamp")

    override suspend fun saveDeviceInfo(deviceInfo: DeviceInfo) {
        dataStore.edit { preferences ->
            preferences[deviceIdKey] = deviceInfo.deviceId
            preferences[carIdKey] = deviceInfo.carId
            preferences[timestampKey] = deviceInfo.timestamp
        }
    }

    override suspend fun getDeviceInfo(): DeviceInfo? {
        return dataStore.data.map { preferences ->
            val deviceId = preferences[deviceIdKey]
            val carId = preferences[carIdKey]
            val timestamp = preferences[timestampKey]
            if (deviceId != null && carId != null && timestamp != null) {
                DeviceInfo(deviceId, carId, timestamp)
            } else {
                null
            }
        }.firstOrNull()
    }

    @SuppressLint("HardwareIds")
    override suspend fun getDeviceId(): String {
        val existingInfo = getDeviceInfo()
        if (existingInfo != null) {
            return existingInfo.deviceId
        }

        val deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Settings.Secure.getString(
                appContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } else {
            try {
                val telephonyManager = appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                    telephonyManager.imei ?: Settings.Secure.getString(
                        appContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                } else {
                    Settings.Secure.getString(
                        appContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                }
            } catch (e: Exception) {
                Settings.Secure.getString(
                    appContext.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            }
        }

        saveDeviceInfo(DeviceInfo(deviceId))
        return deviceId
    }
}