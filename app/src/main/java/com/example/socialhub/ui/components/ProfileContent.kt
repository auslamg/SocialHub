package com.example.socialhub.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialhub.ui.SocialHubScreenPadding
import com.example.socialhub.ui.screens.profile.ProfileUiState

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    showLogout: Boolean,
    onLogout: (() -> Unit)?,
    onCreateProfile: (() -> Unit)?
) {
    AnimatedGradientBackground {
        Column(modifier = Modifier.padding(SocialHubScreenPadding())) {
            Text(
                text = "Profile",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = AppColors.WhiteText
            )
            Text(
                text = "Instagram-style grid with highlights.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.user != null) {
                val profile = uiState.user
                ProfileHeader(
                    name = profile.name,
                    handle = "@${profile.username}",
                    avatarUrl = profile.avatarUrl,
                    bio = profile.bio ?: "",
                    postsCount = profile.postsCount,
                    followersCount = profile.followersCount,
                    followingCount = profile.followingCount
                )
                if (showLogout && onLogout != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentAzure),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Log out", color = AppColors.Gradient2)
                    }
                }
            } else if (!uiState.isLoading) {
                ProfileHeader(
                    name = "Your name",
                    handle = "@guest",
                    avatarUrl = null,
                    bio = "Add a bio to introduce yourself.",
                    postsCount = 0,
                    followersCount = 0,
                    followingCount = 0
                )
                if (showLogout && onCreateProfile != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onCreateProfile,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentAzure),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Sign up", color = AppColors.Gradient2)
                    }
                }
            }
            if (uiState.user != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Recent",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = AppColors.ViridianText
                )
                Spacer(modifier = Modifier.height(10.dp))
                val authorName = uiState.user.name
                val authorHandle = "@${uiState.user.username}"
                val authorAvatar = uiState.user.avatarUrl
                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(uiState.posts) { post ->
                        PostCard(
                            author = authorName,
                            handle = authorHandle,
                            body = post.content,
                            likeCount = post.likeCount,
                            dislikeCount = post.dislikeCount,
                            stamp = formatStamp(post.createdAt),
                            avatarUrl = authorAvatar,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item { Spacer(modifier = Modifier.height(120.dp)) }
                }
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

private fun formatStamp(createdAt: Long): String {
    val now = System.currentTimeMillis()
    val minutes = ((now - createdAt) / 60_000L).coerceAtLeast(0)
    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        minutes < 1_440 -> "${minutes / 60}h"
        else -> "${minutes / 1_440}d"
    }
}
