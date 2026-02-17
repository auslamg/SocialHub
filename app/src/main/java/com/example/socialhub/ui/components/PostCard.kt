package com.example.socialhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PostCard(
    author: String,
    handle: String,
    body: String,
    stamp: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = AppColors.Carbon
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AppColors.Aqua)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text(
                        text = author,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = AppColors.Sand
                    )
                    Text(
                        text = handle,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = AppColors.Slate
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stamp,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = AppColors.Slate
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = body,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = AppColors.Sand
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reply", fontFamily = FontFamily.Monospace, color = AppColors.Slate)
                Text("Repost", fontFamily = FontFamily.Monospace, color = AppColors.Slate)
                Text("Like", fontFamily = FontFamily.Monospace, color = AppColors.Slate)
                Text("Share", fontFamily = FontFamily.Monospace, color = AppColors.Slate)
            }
        }
    }
}
