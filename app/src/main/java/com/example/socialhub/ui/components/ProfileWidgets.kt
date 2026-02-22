package com.example.socialhub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.socialhub.R

@Composable
fun ProfileHeader(
    name: String,
    handle: String,
    avatarUrl: String?,
    bio: String,
    postsCount: Int,
    followersCount: Int,
    followingCount: Int,
    modifier: Modifier = Modifier
) {
    // Header card for profile identity + stats.
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = AppColors.PostCardBG
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(AppColors.AccentAzure)
                ) {
                    if (!avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "$name avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.avatar_placeholder),
                            error = painterResource(R.drawable.avatar_placeholder)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.avatar_placeholder),
                            contentDescription = "$name avatar placeholder",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Column {
                    Text(
                        text = name,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = AppColors.WhiteText
                    )
                    Text(
                        text = handle,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = AppColors.ViridianText
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = bio,
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.sp,
                color = AppColors.WhiteText
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProfileStat(label = "Posts", value = postsCount.toString())
                ProfileStat(label = "Followers", value = followersCount.toString())
                ProfileStat(label = "Following", value = followingCount.toString())
            }
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    // Compact stat used in the profile header row.
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = AppColors.WhiteText
        )
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = AppColors.ViridianText
        )
    }
}
