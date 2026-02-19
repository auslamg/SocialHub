package com.example.socialhub.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.socialhub.R
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.screens.hub.HubScreen
import com.example.socialhub.ui.screens.hub.TrendingScreen
import com.example.socialhub.ui.screens.login.CreateUserScreen
import com.example.socialhub.ui.screens.post.CreatePostScreen
import com.example.socialhub.ui.screens.profile.ProfileScreen

sealed class AppDestination(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int
) {
    data object Hub : AppDestination("hub", "Hub", R.drawable.ic_hub)
    data object Trending : AppDestination("trending", "Trend", R.drawable.ic_trending)
    data object CreatePost : AppDestination("create_post", "Post", R.drawable.ic_create)
    data object Profile : AppDestination("profile", "Profile", R.drawable.ic_profile)
    data object CreateUser : AppDestination("create_user", "Join", R.drawable.ic_create_user)

    @Composable
    fun navColors() = NavigationBarItemDefaults.colors(
        selectedIconColor = AppColors.WhiteText,
        selectedTextColor = AppColors.WhiteText,
        unselectedIconColor = AppColors.ViridianText,
        unselectedTextColor = AppColors.ViridianText,
        indicatorColor = AppColors.AccentAzure
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
