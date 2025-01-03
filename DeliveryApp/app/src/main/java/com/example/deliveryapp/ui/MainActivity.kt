package com.example.deliveryapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.deliveryapp.model.OhtomiRepository
import com.example.deliveryapp.ui.homeView.MainScreen
import com.example.deliveryapp.ui.theme.DeliveryAppTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var ohtomiRepository: OhtomiRepository

    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()

        setContent {
            DeliveryAppTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeliveryAppNavHost(
                        navController = rememberNavController(),
                        startDestination = "main",
                        locationViewModel
                    )
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            //権限が付与された
            getFirebaseToken()
        } else {
            //権限の付与を拒否された
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                //権限が付与されておりすでにPush通知を受け取れる状態
                getFirebaseToken()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                //一度拒否された後の許可ダイアログの再表示をしたい場合
            } else {
                //権限が付与されていないため権限をリクエストする
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            CoroutineScope(Dispatchers.IO).launch {
                val result = ohtomiRepository.postDeviceToken(token)
                result.onSuccess { data ->
                    Log.d("DEBUG", "token: $data")
                }
                Log.d("DEBUG", "token: $token")
            }
        })
    }
}


private fun NavGraphBuilder.mainScreen(locationViewModel: LocationViewModel) {
    navigation(route = "main", startDestination = "main/entry") {
        composable("main/entry") {
            MainScreen(locationViewModel)
        }
    }
}

@Composable
fun DeliveryAppNavHost(
    navController: NavHostController,
    startDestination: String,
    locationViewModel: LocationViewModel
) {
    NavHost(navController = navController, startDestination = startDestination) {
        mainScreen(locationViewModel) // 拡張関数 NavGraphBuilder.mainScreenを呼び出す
    }
}


@Preview(showBackground = true)
@Composable
fun DeliveryAppNavHostPreview() {
    val locationViewModel: LocationViewModel by ComponentActivity().viewModels()
    DeliveryAppTheme {
        DeliveryAppNavHost(
            navController = rememberNavController(),
            startDestination = "main",
            locationViewModel = locationViewModel
        )
    }
}