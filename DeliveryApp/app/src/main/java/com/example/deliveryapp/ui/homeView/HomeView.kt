package com.example.deliveryapp.ui.homeView

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.deliveryapp.R
import com.example.deliveryapp.model.LocationService
import com.example.deliveryapp.ui.LocationViewModel
import com.example.deliveryapp.ui.dialog.CarIdInputDialog
import com.example.deliveryapp.ui.dialog.LocationRequestDialog
import com.example.deliveryapp.ui.rememberLocationPermissionState
import com.example.deliveryapp.ui.sensorListView.SensorDetailView
import com.example.deliveryapp.ui.sensorListView.SensorListView
import com.example.deliveryapp.ui.sensorListView.SensorListViewModel
import com.example.deliveryapp.ui.theme.Typography
import com.google.accompanist.permissions.ExperimentalPermissionsApi
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
fun MainScreen(locationViewModel: LocationViewModel) {
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
                    HomeView(locationViewModel)
                }
                composable("main/list") {
                    SensorListView(navController = nestedNavController)
                }
                composable(
                    "main/detail/{deviceName}",
                    arguments = listOf(navArgument("deviceName") { type = NavType.StringType })
                ) { backStackEntry ->
                    val deviceName = backStackEntry.arguments?.getString("deviceName")
                    Log.d("NavHost", "Navigating to SensorDetailView with deviceName: $deviceName")
                    if (deviceName != null) {
                        SensorDetailView(deviceName = deviceName)
                    } else {
                        Log.e("NavHost", "deviceName is null")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HomeView(viewModel: LocationViewModel) {
    val currentTime = remember { mutableStateOf("") }
    val currentDate = remember { mutableStateOf("") }
    val showCarIdInputDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val locationPermissionState = rememberLocationPermissionState()

    val isTracking by viewModel.isTracking.collectAsState()
    val locationState by viewModel.locationState.collectAsState()
    val deviceId by viewModel.deviceId.collectAsState()
    val carId by viewModel.carId.collectAsState()

    val formattedDate = buildString {
        val dateTime = LocalDateTime.now()
        append(dateTime.monthValue).append("月") // 月
        append(dateTime.dayOfMonth).append("日") // 日
        append("(") // 曜日を括弧で囲む
        append(dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.JAPANESE)) // 日本語の曜日 (短縮)
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

    if (locationPermissionState.shouldOpenLocationRequestDialog) {
        LocationRequestDialog(
            onConfirmClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            },
            onDismissRequest = locationPermissionState::onDismissRequest
        )
    }

    if (showCarIdInputDialog.value) {
        CarIdInputDialog(
            carId = carId ?: 0,
            onConfirmClick = { newCarId ->
                viewModel.registerCarId(newCarId.toInt())
                showCarIdInputDialog.value = false
            },
            onDismissRequest = { showCarIdInputDialog.value = false }
        )
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp)
        ) {
            Button(onClick = { showCarIdInputDialog.value = true }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_fire_truck_24),
                    contentDescription = "Truck",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(32.dp)
                )
                Text(
                    text = "車両ID: ${carId ?: "取得中..."}",
                    style = Typography.titleMedium,
                    color = Color.White
                )
            }
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
        Text(deviceId.toString())
        Text(carId.toString())
        if (isTracking) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(200.dp)
                    .height(64.dp)
                    .padding(8.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
                Text(
                    text = stringResource(R.string.measurement_progress)
                )
            }
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
                onClick = {
                    locationPermissionState.requestLocationPermission()
                    if (locationPermissionState.isLocationGranted) {
                        val intent = Intent(context, LocationService::class.java)
                        ContextCompat.startForegroundService(context, intent)
                    }

                },
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
                onClick = {
//                    if (isLocationTracking) {
                        val intent = Intent(context, LocationService::class.java)
                        context.stopService(intent)
//                    }
                },
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