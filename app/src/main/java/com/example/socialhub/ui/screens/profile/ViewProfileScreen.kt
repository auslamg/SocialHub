package com.example.socialhub.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.socialhub.ui.components.ProfileContent

@Composable
fun ViewProfileScreen(
    viewModel: ViewProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileContent(
        uiState = uiState,
        showLogout = false,
        onLogout = null
    )
}
