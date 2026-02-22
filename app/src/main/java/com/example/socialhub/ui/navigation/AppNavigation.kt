package com.example.socialhub.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.socialhub.R
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.screens.hub.HubScreen
import com.example.socialhub.ui.screens.login.CreateUserScreen
import com.example.socialhub.ui.screens.post.CreatePostScreen
import com.example.socialhub.ui.screens.profile.EditUserScreen
import com.example.socialhub.ui.screens.profile.MyProfileScreen
import com.example.socialhub.ui.screens.profile.ViewProfileScreen
import com.example.socialhub.ui.screens.search.SearchScreen

sealed class AppDestination(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int
) {
    // Bottom nav destinations (route/label/icon) used across the app.
    data object Hub : AppDestination("hub", "Hub", R.drawable.ic_hub)
    data object CreatePost : AppDestination("create_post", "Post", R.drawable.ic_create)
    data object Search : AppDestination("search", "Search", R.drawable.ic_search)
    data object MyProfile : AppDestination("my_profile", "Profile", R.drawable.ic_profile)
    data object EditUser : AppDestination("edit_user", "Edit", R.drawable.ic_profile)
    data object ViewProfile : AppDestination("view_profile/{userId}", "Profile", R.drawable.ic_profile) {
        fun createRoute(userId: Long): String = "view_profile/$userId"
    }
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
    // Central navigation graph. Keep routes in one place for consistency.
    NavHost(
        navController = navController,
        startDestination = AppDestination.Hub.route
    ) {
        composable(AppDestination.Hub.route) { HubScreen(navController) }
        composable(AppDestination.CreatePost.route) { CreatePostScreen() }
        composable(AppDestination.Search.route) { SearchScreen(navController) }
        composable(AppDestination.MyProfile.route) { MyProfileScreen(navController) }
        composable(AppDestination.EditUser.route) { EditUserScreen(navController) }
        composable(
            route = AppDestination.ViewProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { ViewProfileScreen(navController) }
        composable(AppDestination.CreateUser.route) { CreateUserScreen(navController) }
    }
}
