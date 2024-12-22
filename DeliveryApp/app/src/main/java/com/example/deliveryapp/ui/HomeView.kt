package com.example.deliveryapp.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.deliveryapp.OhtomiApplication
import com.example.deliveryapp.R
import com.example.deliveryapp.model.OhtomiRepository
import com.example.deliveryapp.ui.dialog.CarIdInputDialog
import com.example.deliveryapp.ui.dialog.LocationRequestDialog
import com.example.deliveryapp.ui.theme.DeliveryAppTheme
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
                    HomeView(locationViewModel = locationViewModel)
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HomeView(locationViewModel: LocationViewModel) {
    val currentTime = remember { mutableStateOf("") }
    val currentDate = remember { mutableStateOf("") }
//    var carId = remember { mutableIntStateOf(555) }
    var showCarIdInputDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val locationPermissionState = rememberLocationPermissionState()
    val isLocationTracking = locationViewModel.isLocationTracking.value
    val carId by locationViewModel.carId.observeAsState()

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
            carId = carId,
            onConfirmClick = { newCarId ->
                val intent = Intent(context, LocationService::class.java).apply {
                    action = LocationService.ACTION_UPDATE_CAR_ID
                    putExtra(LocationService.EXTRA_CAR_ID, newCarId)
                }
                context.startService(intent)
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
                    text = "車両ID: ${carId}",
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
                    .background(
                        if (isLocationTracking) {
                            Color.Red
                        } else {
                            Color.Green
                        }
                    )
            )
            Text(
                text = stringResource(R.string.measurement_progress),
                modifier = Modifier.background(Color.Cyan)
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
                onClick = {
                    locationPermissionState.requestLocationPermission()
//                    if (locationPermissionState.isLocationGranted && !isLocationTracking) {
//                        locationViewModel.startLocationService()
                        val intent = Intent(context, LocationService::class.java)
                        ContextCompat.startForegroundService(context, intent)
//                    }

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
//                        locationViewModel.stopLocationService()
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

@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val context = LocalContext.current
//    val locationViewModel: LocationViewModel = viewModel(
//        factory = LocationViewModelFactory(context)
//    )

    DeliveryAppTheme {
        MainScreen(locationViewModel = LocationViewModel(Application()))
    }
}