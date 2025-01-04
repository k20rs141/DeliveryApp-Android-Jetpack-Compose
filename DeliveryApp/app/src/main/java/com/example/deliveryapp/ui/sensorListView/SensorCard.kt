package com.example.deliveryapp.ui.sensorListView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.deliveryapp.R
import com.example.deliveryapp.data.SensorData
import com.example.deliveryapp.ui.theme.Typography

@Composable
fun SensorCard(sensor: SensorData, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
//                    .height(32.dp)
            ) {
                Text(
                    text = sensor.deviceName,
                    style = Typography.bodyMedium
                )
                Spacer(Modifier.weight(1f))
                val wifiImage = when {
//                    sensor.rssi >= -50 -> R.drawable.baseline_wifi_24          // 非常に強い: -50dBm以上
//                    sensor.rssi >= -70 -> R.drawable.baseline_wifi_2_bar_24    // 普通: -70dBm以上
//                    sensor.rssi >= -80 -> R.drawable.baseline_wifi_1_bar_24    // 弱い: -80dBm以上
//                    else -> R.drawable.baseline_wifi_off_24             // 無し: -80dBm未満
                    sensor.wifiEnd == 1 -> R.drawable.baseline_wifi_off_24
                    sensor.rssi >= -40 -> R.drawable.baseline_wifi_24
                    sensor.rssi >= -42 -> R.drawable.baseline_wifi_2_bar_24
                    sensor.rssi >= -49 -> R.drawable.baseline_wifi_1_bar_24
                    else -> R.drawable.baseline_wifi_1_bar_24
                }

                Image(
                    painter = painterResource(id = wifiImage),
                    contentDescription = when {
                        sensor.rssi >= -50 -> "非常に強い電波強度"
                        sensor.rssi >= -70 -> "普通な電波強度"
                        sensor.rssi >= -80 -> "弱い電波強度"
                        sensor.wifiEnd == 0 -> "電波強度なし"
                        else -> "弱い電波強度"
                    },
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(32.dp)
                )
            }
            val (imageRes, unit) = when (sensor.co2Sensor) {
                1 -> Pair(R.drawable.co2, "ppm")
                0 -> Pair(R.drawable.baseline_device_thermostat_24, "°C")
                else -> Pair(R.drawable.baseline_device_thermostat_24, "°C")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Cyan)
            ) {
                Image(
                    painter = painterResource(id =  imageRes),
                    contentDescription = if (sensor.co2Sensor == 1) "CO2" else "Temperature",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.Red)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (sensor.co2Sensor == 1) sensor.co2.toString() else sensor.temperature.toString(),
                    style = Typography.titleLarge
                )
                Text(
                    text = unit,
                    style = Typography.bodyMedium
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Cyan)
            ) {
                Image(
                    painter = painterResource(id =  R.drawable.humidity),
                    contentDescription = "humidity",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Red)
                )
//                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sensor.humidity.toString(),
                    style = Typography.bodyLarge
                )
                Text(
                    text = "%",
                    style = Typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(32.dp))
                Image(
                    painter = painterResource(id =  R.drawable.baseline_air_24),
                    contentDescription = "pressure",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Red)
                )
                Text(
                    text = sensor.pressure.toString(),
                    style = Typography.bodyLarge
                )
                Text(
                    text = "hPa",
                    style = Typography.bodyMedium
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id =  R.drawable.round_access_time_24),
                    contentDescription = "modified",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Red)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sensor.modified,
                    style = Typography.bodySmall
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = "(${sensor.build.toString()})",
                    style = Typography.bodySmall
                )
            }
        }
    }
}