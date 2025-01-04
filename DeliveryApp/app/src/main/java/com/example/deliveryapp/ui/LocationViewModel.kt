package com.example.deliveryapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.LocationData
import com.example.deliveryapp.model.DeviceInfo
import com.example.deliveryapp.model.DeviceInfoRepository
import com.example.deliveryapp.model.OhtomiRepository
import com.example.deliveryapp.ui.sensorListView.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val ohtomiRepository: OhtomiRepository,
    private val deviceInfoRepository: DeviceInfoRepository
): ViewModel() {
    private val _locationState = MutableStateFlow<UiState<LocationData>>(UiState.Initial)
    val locationState: StateFlow<UiState<LocationData>> = _locationState.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _deviceId = MutableStateFlow<String?>(null)
    val deviceId: StateFlow<String?> = _deviceId.asStateFlow()

    private val _carId = MutableStateFlow<Int>(0)
    val carId: StateFlow<Int> = _carId.asStateFlow()

    init {
        initializeDeviceId()
    }

    private fun initializeDeviceId() {
        viewModelScope.launch {
            try {
                val imei = deviceInfoRepository.getDeviceId()
                deviceInfoRepository.saveDeviceInfo(deviceInfo = DeviceInfo(imei))
                _deviceId.value = imei
                ohtomiRepository.getDeviceData(imei, _carId.value)
                    .onSuccess {
                        _carId.value = it
                        deviceInfoRepository.saveDeviceInfo(
                            deviceInfo = DeviceInfo(imei, it)
                        )
                    }
                    .onFailure { error ->
                        _locationState.value = UiState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: Exception) {
                _locationState.value = UiState.Error("Failed to get device ID")
            }
        }
    }

    fun registerCarId(carId: Int) {
        viewModelScope.launch {
            try {
                val deviceId = deviceInfoRepository.getDeviceInfo()?.deviceId
                if (deviceId != null) {
                    ohtomiRepository.getDeviceData(deviceId, carId)
                        .onSuccess {
                            _carId.value = it
                            deviceInfoRepository.saveDeviceInfo(
                                deviceInfo = DeviceInfo(deviceId, it)
                            )
                        }
                        .onFailure { error ->
                            _locationState.value = UiState.Error(error.message ?: "Unknown error")
                        }
                }
            } catch (e: Exception) {
                _locationState.value = UiState.Error("Failed to get device ID")
            }
        }
    }

    fun locationTracking(isTracking: Boolean) {
        _isTracking.value = isTracking
    }
}