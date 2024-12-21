package com.example.deliveryapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deliveryapp.R
import com.example.deliveryapp.data.SensorData
import com.example.deliveryapp.ui.component.SensorCard
import com.example.deliveryapp.ui.theme.DeliveryAppTheme

@Composable
fun SensorListView(
    sensorDataUiState: SensorDataUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (sensorDataUiState) {
        is SensorDataUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is SensorDataUiState.Success -> SensorListScreen(
            sensorDataUiState.sensors, modifier = modifier.fillMaxWidth()
        )
        is SensorDataUiState.Error -> ErrorScreen(retryAction, modifier = modifier.width(64.dp))
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
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
fun SensorListScreen(sensorData: List<SensorData>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.background(MaterialTheme.colorScheme.primary)) {
        items(items = sensorData, key = { it.deviceId }) { sensor ->
            SensorCard(
                sensor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
        }
    }
}