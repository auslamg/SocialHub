package com.example.socialhub.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.socialhub.ui.components.ProfileContent
import com.example.socialhub.ui.navigation.AppDestination

/**
 * Screen for the current user's profile and related actions.
 */
@Composable
fun MyProfileScreen(
    navController: NavHostController,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
    // Profile is driven by the current user session (front-end state).
    val uiState by viewModel.uiState.collectAsState()

    ProfileContent(
        uiState = uiState,
        showLogout = true,
        onLogout = viewModel::logout,
        onCreateProfile = {
            navController.navigate(AppDestination.CreateUser.route) {
                launchSingleTop = true
                popUpTo(AppDestination.MyProfile.route) { inclusive = true }
            }
        },
        onHandleClick = { userId ->
            navController.navigate(AppDestination.ViewProfile.createRoute(userId))
        },
        onEditProfile = {
            navController.navigate(AppDestination.EditUser.route) {
                launchSingleTop = true
            }
        },
        onEditPost = { postId ->
            navController.navigate(AppDestination.EditPost.createRoute(postId))
        }
    )
}
