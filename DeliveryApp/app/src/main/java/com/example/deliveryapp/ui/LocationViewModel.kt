package com.example.deliveryapp.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.model.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    val isLocationTracking = mutableStateOf(false)
    private val _carId = MutableLiveData<Int?>()
    val carId: LiveData<Int?> = _carId

    private val carIdReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CAR_ID_UPDATED") {
                val newCarId = intent.getIntExtra("car_id", -1)
                if (newCarId != -1) {
                    _carId.value = newCarId
                }
            }
        }
    }

    init {
        registerReceiver()
    }

    private fun registerReceiver() {
        val filter = IntentFilter("CAR_ID_UPDATED")
        getApplication<Application>().registerReceiver(carIdReceiver, filter)
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(carIdReceiver)
    }

    fun updateCarId(newCarId: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            val intent = Intent(getApplication(), LocationService::class.java).apply {
                action = LocationService.ACTION_UPDATE_CAR_ID
                putExtra(LocationService.EXTRA_CAR_ID, newCarId)
            }
            getApplication<Application>().startService(intent)
        }
    }

    fun fetchedIMEI() {
        viewModelScope.launch {

        }
    }
}