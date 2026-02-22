package com.example.socialhub.ui.screens.post

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.socialhub.ui.SocialHubScreenPadding
import com.example.socialhub.ui.components.AnimatedGradientBackground
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.components.DarkOutlinedTextField
import com.example.socialhub.ui.components.ProfileHeader

@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedGradientBackground {
        Column(
            modifier = Modifier
                .padding(SocialHubScreenPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Create post",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = AppColors.WhiteText
            )
            Text(
                text = "Thoughts under 280 characters.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(16.dp))
            val userName = uiState.userName
            val userHandle = uiState.userHandle
            if (!uiState.isGuest && userName != null && userHandle != null) {
                ProfileHeader(
                    name = userName,
                    handle = userHandle,
                    avatarUrl = uiState.avatarUrl,
                    bio = uiState.userBio ?: "",
                    postsCount = uiState.postsCount,
                    followersCount = uiState.followersCount,
                    followingCount = uiState.followingCount
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AppColors.AccentAzure)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            DarkOutlinedTextField(
                value = uiState.content,
                onValueChange = viewModel::onContentChange,
                label = "What's happening?",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                outlineColor = AppColors.AccentAqua,
                supportingText = uiState.contentError,
                isError = uiState.contentError != null
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${uiState.charCount}/280",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            if (uiState.isGuest) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Sign in to create a post.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = AppColors.ViridianText
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = viewModel::submitPost,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentAqua),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.canPost
            ) {
                Text("Post now", color = AppColors.Gradient2)
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
