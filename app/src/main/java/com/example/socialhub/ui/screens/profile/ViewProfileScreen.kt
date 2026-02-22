package com.example.socialhub.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.socialhub.ui.components.ProfileContent
import com.example.socialhub.ui.navigation.AppDestination

@Composable
fun ViewProfileScreen(
    navController: NavHostController,
    viewModel: ViewProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileContent(
        uiState = uiState,
        showLogout = false,
        onLogout = null,
        onCreateProfile = null,
        onHandleClick = { userId ->
            navController.navigate(AppDestination.ViewProfile.createRoute(userId))
        },
        onEditProfile = null
    )
}
