package com.example.deliveryapp.ui.sensorListView

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.deliveryapp.R
import com.example.deliveryapp.data.SensorData
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SensorListView(navController: NavController, viewModel: SensorListViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    val sensorState by viewModel.sensorState.collectAsState()

    when (sensorState) {
        is UiState.Initial -> {

        }
        is UiState.Loading -> {
            LoadingScreen()
        }
        is UiState.Success -> {
            val sensors = (sensorState as UiState.Success<List<SensorData>>).data
            SensorListScreen(
                sensors,
                onClick = { deviceName ->
                    Log.d("Navigation", "Navigating to detail with deviceName: $deviceName")
                    val encodedDeviceName = URLEncoder.encode(deviceName, StandardCharsets.UTF_8.toString())
                    navController.navigate("main/detail/$encodedDeviceName")
                },
                modifier = modifier.fillMaxSize()
            )
        }
        is UiState.Error -> {
            ErrorScreen(retryAction = { viewModel.fetchSensorData(508, 1) }, modifier = modifier.fillMaxSize())
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun SensorListScreen(sensorData: List<SensorData>, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.background(MaterialTheme.colorScheme.primary)) {
        items(items = sensorData, key = { it.deviceId }) { sensor ->
            SensorCard(
                sensor,
                onClick = { onClick(sensor.deviceName) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp)
            )
        }
    }
}