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
import com.example.socialhub.ui.screens.hub.SamplePost
import com.example.socialhub.ui.screens.profile.ProfileUiState

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    showLogout: Boolean,
    onLogout: (() -> Unit)?
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
                Text(
                    text = "No profile found.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = AppColors.ViridianText
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Recent",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(samplePosts()) { post ->
                    PostCard(
                        author = post.author,
                        handle = post.handle,
                        body = post.body,
                        stamp = post.stamp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item { Spacer(modifier = Modifier.height(120.dp)) }
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

private fun samplePosts(): List<SamplePost> = listOf(
    SamplePost("Dylan Shore", "@iris", "Exploring quiet builds for loud ideas.", "2m"),
    SamplePost("Dylan Shore", "@novap", "Late-night sprint, early-morning glow.", "7m"),
    SamplePost("Dylan Shore", "@theo", "Shipping, but with intention.", "11m")
)
