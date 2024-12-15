package com.example.deliveryapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.deliveryapp.ui.theme.DeliveryAppTheme

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
fun HomeView() {
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
        Box(modifier = Modifier.padding(it)) {
            NavHost(
                navController = nestedNavController,
                startDestination = "main/home",
                modifier = Modifier
            ) {
                composable("main/home") {
                    Text("main/home")
                }
                composable("main/list") {
                    Text("main/list")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    DeliveryAppTheme {
        HomeView()
    }
}