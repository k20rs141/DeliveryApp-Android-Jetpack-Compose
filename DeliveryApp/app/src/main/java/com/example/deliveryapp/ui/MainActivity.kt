package com.example.deliveryapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.deliveryapp.ui.homeView.MainScreen
import com.example.deliveryapp.ui.theme.DeliveryAppTheme

class MainActivity : ComponentActivity() {
    val locationViewModel: LocationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val serviceIntent = Intent(this, LocationViewModel::class.java)
        startService(serviceIntent)

        setContent {
            DeliveryAppTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeliveryAppNavHost(viewModel = locationViewModel)
                }
            }
        }
    }
}

private fun NavGraphBuilder.mainScreen(viewModel: LocationViewModel) {
    navigation(route = "main", startDestination = "main/entry") {
        composable("main/entry") {
//            val context = LocalContext.current
//            val viewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)
//            val locationViewModel: LocationViewModel = viewModel(
//                factory = LocationViewModel(context = context, repository = viewModel)
//            )
            MainScreen(locationViewModel = viewModel)
        }
    }
}

@Composable
fun DeliveryAppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main",
    viewModel: LocationViewModel
) {
    NavHost(navController = navController, startDestination = startDestination) {
        mainScreen(viewModel = viewModel) // 拡張関数 NavGraphBuilder.mainScreenを呼び出す
    }
}


@Preview(showBackground = true)
@Composable
fun DeliveryAppNavHostPreview() {
    val locationViewModel: LocationViewModel by ComponentActivity().viewModels()
    DeliveryAppTheme {
        DeliveryAppNavHost(
            viewModel = locationViewModel
        )
    }
}