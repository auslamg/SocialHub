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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun CreateUserScreen() {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

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
                value = name,
                onValueChange = { name = it },
                label = "Name",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            DarkOutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            DarkOutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "Bio",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentAzure),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
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
