package com.example.socialhub.ui.screens.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import com.example.socialhub.ui.components.CreatePostCard

/**
 * Screen for composing a new post, with validation and character count.
 */
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
            CreatePostCard(
                author = if (!uiState.isGuest && userName != null) userName else "Guest",
                handle = if (!uiState.isGuest && userHandle != null) userHandle else "@guest",
                stamp = "Draft",
                avatarUrl = if (uiState.isGuest) null else uiState.avatarUrl,
                content = uiState.content,
                onContentChange = viewModel::onContentChange,
                supportingText = uiState.contentError,
                isError = uiState.contentError != null,
                modifier = Modifier.fillMaxWidth()
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
