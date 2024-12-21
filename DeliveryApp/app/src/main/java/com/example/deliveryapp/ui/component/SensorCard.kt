package com.example.deliveryapp.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deliveryapp.data.SensorData
import com.example.deliveryapp.ui.theme.Typography

@Composable
fun SensorCard(sensor: SensorData, modifier: Modifier = Modifier) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = sensor.co2,
                style = Typography.titleLarge
            )
            Text(
                text = sensor.temperature.toString(),
                style = Typography.titleLarge
            )
        }
    }
}