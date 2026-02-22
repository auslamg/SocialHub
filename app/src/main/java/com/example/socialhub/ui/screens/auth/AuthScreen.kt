package com.example.socialhub.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.socialhub.ui.SocialHubScreenPadding
import com.example.socialhub.ui.components.AnimatedGradientBackground
import com.example.socialhub.ui.components.AppColors

@Composable
fun AuthScreen(
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedGradientBackground {
        Column(modifier = Modifier.padding(SocialHubScreenPadding())) {
            Text(
                text = "Auth",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = AppColors.WhiteText
            )
            Text(
                text = "Sign in to sync your profile or log out to switch accounts.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.statusText,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = if (uiState.isLoggedIn) AppColors.AccentAqua else AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentAzure),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log in", color = AppColors.Gradient2)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Gradient2),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log out", color = AppColors.WhiteText)
            }
        }
    }
}
