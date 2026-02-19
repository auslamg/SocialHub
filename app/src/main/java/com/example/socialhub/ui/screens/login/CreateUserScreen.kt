package com.example.socialhub.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.socialhub.ui.SocialHubScreenPadding
import com.example.socialhub.ui.components.AnimatedGradientBackground
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.components.DarkOutlinedTextField
import com.example.socialhub.ui.navigation.AppDestination

@Composable
fun CreateUserScreen(
    navController: NavHostController,
    viewModel: CreateUserViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.navigation.collect {
            navController.navigate(AppDestination.Profile.route) {
                launchSingleTop = true
                popUpTo(AppDestination.CreateUser.route) { inclusive = true }
            }
        }
    }

    AnimatedGradientBackground {
        Column(
            modifier = Modifier
                .padding(SocialHubScreenPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Create your profile",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = AppColors.WhiteText
            )
            Text(
                text = "Or keep it light and join as a guest.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(AppColors.AccentAqua)
            )
            Spacer(modifier = Modifier.height(16.dp))
            DarkOutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = "Name",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            DarkOutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                label = "Username",
                modifier = Modifier.fillMaxWidth(),
                supportingText = uiState.usernameError,
                isError = uiState.usernameError != null
            )
            Spacer(modifier = Modifier.height(12.dp))
            DarkOutlinedTextField(
                value = uiState.avatarUrl,
                onValueChange = viewModel::onAvatarUrlChange,
                label = "Avatar URL",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            DarkOutlinedTextField(
                value = uiState.bio,
                onValueChange = viewModel::onBioChange,
                label = "Bio",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = viewModel::registerUser,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentAzure),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving &&
                    uiState.name.isNotBlank() &&
                    uiState.username.isNotBlank() &&
                    uiState.usernameError == null
            ) {
                Text("Create profile", color = AppColors.Gradient2)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Gradient2),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue as guest", color = AppColors.WhiteText)
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
