package com.example.socialhub.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    SocialHubTheme {
        val navController = rememberNavController()
        val destinations = listOf(
            AppDestination.Hub,
            AppDestination.Trending,
            AppDestination.CreatePost,
            AppDestination.Profile,
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
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(AppDestination.Hub.route) {
                                        saveState = true
                                    }
                                }
                            },
                            icon = {
                                Text(
                                    text = destination.iconText,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp
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
            Box(modifier = Modifier.padding(padding)) {
                SocialHubNavHost(navController = navController)
            }
        }
    }
}

@Composable
fun SocialHubScreenPadding(): PaddingValues = PaddingValues(
    start = 20.dp,
    end = 20.dp,
    top = 18.dp,
    bottom = 90.dp
)
