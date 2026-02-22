package com.example.socialhub.ui.screens.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.socialhub.ui.SocialHubScreenPadding
import com.example.socialhub.ui.components.AnimatedGradientBackground
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.components.CreatePostCard

@Composable
fun EditPostScreen(
    navController: NavHostController,
    viewModel: EditPostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect {
            navController.popBackStack()
        }
    }

    AnimatedGradientBackground {
        Column(
            modifier = Modifier
                .padding(SocialHubScreenPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Edit post",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = AppColors.WhiteText
            )
            Text(
                text = "Keep it under 280 characters.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(16.dp))
            CreatePostCard(
                author = uiState.userName ?: "User",
                handle = uiState.userHandle ?: "@user",
                stamp = "Edit",
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
            if (!uiState.postExists) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Post not found.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = AppColors.ViridianText
                )
            } else if (!uiState.isOwner) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "You can only edit your own posts.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = AppColors.ViridianText
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = viewModel::saveChanges,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentAqua),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.canSave
            ) {
                Text("Save changes", color = AppColors.Gradient2)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PostCardBG),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = AppColors.WhiteText)
                }
                Spacer(modifier = Modifier.size(12.dp))
                Button(
                    onClick = viewModel::deletePost,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f),
                    enabled = uiState.canDelete
                ) {
                    Text("Delete post", color = AppColors.WhiteText)
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
