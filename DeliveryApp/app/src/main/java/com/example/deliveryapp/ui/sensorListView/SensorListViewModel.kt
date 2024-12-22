package com.example.deliveryapp.ui.sensorListView

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
import com.example.deliveryapp.model.OhtomiRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface SensorDataUiState {
    data class Success(val sensors: List<SensorData>) : SensorDataUiState
    object Error : SensorDataUiState
    object Loading : SensorDataUiState
}

class SensorListViewModel(
    private val ohtomiRepository: OhtomiRepository
): ViewModel() {
    var sensorUiState: SensorDataUiState by mutableStateOf(SensorDataUiState.Loading)
        private set

    init {
        getSensorData()
    }

    fun getSensorData() {
        viewModelScope.launch {
            sensorUiState = SensorDataUiState.Loading
            sensorUiState = try {
                SensorDataUiState.Success(ohtomiRepository.getSensorData(carId = 508, limit = 1))
            } catch (e: IOException) {
                SensorDataUiState.Error
            } catch (e: HttpException) {
                SensorDataUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as OhtomiApplication)
                val ohtomiRepository = application.container.ohtomiRepository
                SensorListViewModel(ohtomiRepository = ohtomiRepository)
            }
        }
    }
}