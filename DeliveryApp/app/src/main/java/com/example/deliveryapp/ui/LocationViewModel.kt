package com.example.deliveryapp.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.deliveryapp.OhtomiApplication
import com.example.deliveryapp.model.OhtomiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    val isLocationTracking = mutableStateOf(false)
    private val _carId = MutableLiveData<Int?>()
    val carId: LiveData<Int?> = _carId

    private val carIdReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationService.ACTION_UPDATE_CAR_ID) {
                val newCarId = intent.getIntExtra(LocationService.EXTRA_CAR_ID, -1)
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
        val intentFilter = IntentFilter(LocationService.ACTION_UPDATE_CAR_ID)
        getApplication<Application>().registerReceiver(carIdReceiver, intentFilter)
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(carIdReceiver)
    }
}
