package com.example.deliveryapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.deliveryapp.R
import com.example.deliveryapp.ui.theme.DeliveryAppTheme
import com.example.deliveryapp.ui.theme.Typography
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

enum class MainScreenTab(
    val id: String,
    val icon: ImageVector,
    val label: String
) {
    Home(
        id = "main/home",
        icon = Icons.Default.Home,
        label = "Home"
    ),
    List(
        id = "main/list",
        icon = Icons.AutoMirrored.Filled.List,
        label = "List"
    )
}

@Composable
fun MainScreen() {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentTab = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainScreenTab.entries.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentTab == item.id,
                        onClick = { nestedNavController.navigate(item.id) }
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(it)
        ) {
            NavHost(
                navController = nestedNavController,
                startDestination = "main/home",
                modifier = Modifier
            ) {
                composable("main/home") {
                    HomeView()
                }
                composable("main/list") {
                    val sensorListViewModel: SensorListViewModel = viewModel(factory = SensorListViewModel.Factory)
                    SensorListView(
                        sensorDataUiState = sensorListViewModel.sensorUiState,
                        retryAction = sensorListViewModel::getSensorData
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeView() {
    val currentTime = remember { mutableStateOf("") }
    val currentDate = remember { mutableStateOf("") }
    val carId = remember { mutableStateOf("400") }

    val formattedDate = buildString {
        val currentDate = LocalDateTime.now()
        append(currentDate.monthValue).append("月") // 月
        append(currentDate.dayOfMonth).append("日") // 日
        append("(") // 曜日を括弧で囲む
        append(currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.JAPANESE)) // 日本語の曜日 (短縮)
        append(")")
    }

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()
            // 日付を FULL フォーマットで取得
            currentDate.value = now.toLocalDate()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            // 時刻をフォーマット
            currentTime.value = now.toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.aligned(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_fire_truck_24),
                contentDescription = "Truck",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(32.dp)
//                    .padding(horizontal = 16.dp)
            )
            Text(
                text = "車両ID: ${carId.value}",
                style = Typography.titleMedium,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = formattedDate,
                style = Typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = currentTime.value,
                style = Typography.titleLarge,
                color = Color.Black
            )
        }
        Image(
            painter = painterResource(id = R.drawable.motto),
            contentDescription = "Ohtomi Motto",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 24.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* 開始ボタンの処理 */ },
                shape = RoundedCornerShape(size = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    "開始",
                    style = Typography.bodyLarge
                )

            }
            Button(
                onClick = { /* 終了ボタンの処理 */ },
                shape = RoundedCornerShape(size = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    "終了",
                    style = Typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    DeliveryAppTheme {
        MainScreen()
    }
}