package com.example.socialhub.ui.screens.profile

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.socialhub.ui.components.ProfileContent
import com.example.socialhub.ui.navigation.AppDestination

@Composable
fun MyProfileScreen(
    navController: NavHostController,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
    // Profile is driven by the current user session (front-end state).
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        // If no current user, redirect to Create User.
        if (!uiState.isLoading && uiState.user == null) {
            Toast.makeText(
                context,
                "Create a profile to continue",
                Toast.LENGTH_SHORT
            ).show()
            navController.navigate(AppDestination.CreateUser.route) {
                launchSingleTop = true
                popUpTo(AppDestination.MyProfile.route) { inclusive = true }
            }
        }
    }

    ProfileContent(
        uiState = uiState,
        showLogout = true,
        onLogout = viewModel::logout
    )
}
