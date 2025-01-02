package com.example.deliveryapp.ui.sensorListView

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.deliveryapp.OhtomiApplication
import com.example.deliveryapp.data.SensorData
import com.example.deliveryapp.model.DeviceInfoRepository
import com.example.deliveryapp.model.OhtomiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed interface SensorDataUiState {
    data class Success(val sensors: List<SensorData>) : SensorDataUiState
    object Error : SensorDataUiState
    object Loading : SensorDataUiState
}

sealed class UiState<out T> {
    object Initial : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String, val code: Int? = null) : UiState<Nothing>()
}

@HiltViewModel
class SensorListViewModel  @Inject constructor(
    private val ohtomiRepository: OhtomiRepository,
    private val deviceInfoRepository: DeviceInfoRepository
): ViewModel() {
//    var sensorUiState: SensorDataUiState by mutableStateOf(SensorDataUiState.Loading)
//        private set

    private val _sensorState = MutableStateFlow<UiState<List<SensorData>>>(UiState.Initial)
    val sensorState: StateFlow<UiState<List<SensorData>>> = _sensorState

    init {
        viewModelScope.launch {
            val carId = deviceInfoRepository.getDeviceInfo()?.carId
            if (carId != null) {
//                fetchSensorData(carId, 1)
                fetchSensorData(508,1) // テスト用
            }
        }
    }

    fun fetchSensorData(carId: Int, limit: Int) {
        viewModelScope.launch {
            _sensorState.value = UiState.Loading
            val result = ohtomiRepository.getSensorData(carId, limit)
            _sensorState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unknown Error") }
            )
        }
    }
}