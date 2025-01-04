package com.example.deliveryapp.ui.sensorListView

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deliveryapp.data.SensorData


@Composable
fun SensorDetailView(viewModel: SensorListViewModel = hiltViewModel(), deviceName: String) {
    val sensorDetailState by viewModel.sensorDetailState.collectAsState()

    LaunchedEffect(deviceName) {
        viewModel.fetchSensorDetailData(deviceName)
    }

    when (val state = sensorDetailState) {
        is UiState.Loading -> {
            LoadingScreen()
            Log.d("SensorDetailData", "Loading: ")
        }
        is UiState.Success -> {
            val sensorDetail = state.data
            Log.d("SensorDetailData", "SensorDetailData: ${sensorDetail}")
            SensorDetailScreen(sensorDetail = sensorDetail, modifier = Modifier.fillMaxSize())
        }
        is UiState.Error -> {
            Log.d("SensorDetailData", "Error: ")
            ErrorScreen(
                retryAction = { viewModel.fetchSensorDetailData(deviceName) },
                modifier = Modifier.fillMaxSize()
            )
        }
        else -> {
            Log.d("SensorDetailData", "ELSE: ")
        }
    }
}
@Composable
fun SensorDetailScreen(sensorDetail: List<SensorData>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "センサー詳細", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "デバイスID: ${sensorDetail[0].deviceId}", style = MaterialTheme.typography.bodyLarge)
        // 他の詳細情報を表示
    }
}
