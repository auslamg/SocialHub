package com.example.socialhub.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialhub.ui.SocialHubScreenPadding
import com.example.socialhub.ui.components.AnimatedGradientBackground
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.components.ProfileHeader

@Composable
fun ProfileScreen() {
    AnimatedGradientBackground {
        Column(modifier = Modifier.padding(SocialHubScreenPadding())) {
            Text(
                text = "Profile",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = AppColors.Sand
            )
            Text(
                text = "Instagram-style grid with highlights.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.Slate
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileHeader(
                name = "Dylan Shore",
                handle = "@dylans",
                bio = "Building tiny social rituals. Ambient, thoughtful, slow."
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Recent",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.Slate
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                items((1..12).toList()) { item ->
                    Spacer(
                        modifier = Modifier
                            .height(96.dp)
                            .background(AppColors.Carbon, RoundedCornerShape(12.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
