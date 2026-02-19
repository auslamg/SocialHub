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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialhub.ui.SocialHubScreenPadding
import com.example.socialhub.ui.components.AnimatedGradientBackground
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.components.DarkOutlinedTextField

@Composable
fun CreatePostScreen() {
    var content by remember { mutableStateOf("") }

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
            Spacer(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AppColors.AccentAzure)
            )
            Spacer(modifier = Modifier.height(16.dp))
            DarkOutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = "What's happening?",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                outlineColor = AppColors.AccentAqua
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${content.length}/280",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentAqua),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Post now", color = AppColors.Gradient2)
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
