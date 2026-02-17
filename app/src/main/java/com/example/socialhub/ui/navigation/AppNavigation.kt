package com.example.socialhub.ui.navigation

import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.screens.hub.HubScreen
import com.example.socialhub.ui.screens.hub.TrendingScreen
import com.example.socialhub.ui.screens.login.CreateUserScreen
import com.example.socialhub.ui.screens.post.CreatePostScreen
import com.example.socialhub.ui.screens.profile.ProfileScreen

sealed class AppDestination(
    val route: String,
    val label: String,
    val iconText: String
) {
    data object Hub : AppDestination("hub", "Hub", "H")
    data object Trending : AppDestination("trending", "Trend", "T")
    data object CreatePost : AppDestination("create_post", "Post", "+")
    data object Profile : AppDestination("profile", "Profile", "P")
    data object CreateUser : AppDestination("create_user", "Join", "U")

    @Composable
    fun navColors() = NavigationBarItemDefaults.colors(
        selectedIconColor = AppColors.Sand,
        selectedTextColor = AppColors.Sand,
        unselectedIconColor = AppColors.Slate,
        unselectedTextColor = AppColors.Slate,
        indicatorColor = AppColors.Carbon
    )
}

@Composable
fun SocialHubNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Hub.route
    ) {
        composable(AppDestination.Hub.route) { HubScreen() }
        composable(AppDestination.Trending.route) { TrendingScreen() }
        composable(AppDestination.CreatePost.route) { CreatePostScreen() }
        composable(AppDestination.Profile.route) { ProfileScreen() }
        composable(AppDestination.CreateUser.route) { CreateUserScreen() }
    }
}
