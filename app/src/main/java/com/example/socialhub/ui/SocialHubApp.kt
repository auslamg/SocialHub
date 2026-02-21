package com.example.socialhub.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.navigation.AppDestination
import com.example.socialhub.ui.navigation.SocialHubNavHost
import com.example.socialhub.ui.theme.SocialHubTheme

@Composable
fun SocialHubApp() {
    // Top-level theme + navigation scaffold for the entire app.
    SocialHubTheme {
        val navController = rememberNavController()
        // Bottom nav destinations; order defines the bar layout.
        val destinations = listOf(
            AppDestination.Hub,
            AppDestination.Trending,
            AppDestination.CreatePost,
            AppDestination.Search,
            AppDestination.MyProfile,
            AppDestination.CreateUser
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = AppColors.Gradient1,
            bottomBar = {
                NavigationBar(
                    containerColor = AppColors.PostCardBG,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    destinations.forEach { destination ->
                        // Mark the current route as selected to drive colors.
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                // Single-top keeps one instance per destination in the back stack.
                                navController.navigate(destination.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(AppDestination.Hub.route) {
                                        saveState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(22.dp),
                                    painter = painterResource(id = destination.iconRes),
                                    contentDescription = destination.label
                                )
                            },
                            label = {
                                Text(
                                    text = destination.label,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            },
                            colors = destination.navColors()
                        )
                    }
                }
            }
        ) { padding ->
            // Host composable destinations.
            Box(modifier = Modifier.padding(padding)) {
                SocialHubNavHost(navController = navController)
            }
        }
    }
}

@Composable
fun SocialHubScreenPadding(): PaddingValues = PaddingValues(
    // Common in-screen padding to align content across screens.
    start = 20.dp,
    end = 20.dp,
    top = 18.dp,
    bottom = 90.dp
)
