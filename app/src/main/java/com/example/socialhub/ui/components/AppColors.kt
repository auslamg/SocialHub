package com.example.socialhub.ui.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

// Centralized palette used across Compose screens.
object AppColors {
    val Gradient1 = Color(0xFF222A3B)
    val Gradient2 = Color(0xFF1D3D59)
    val Gradient3 = Color(0xFF255270)

    val PostCardBG = Color(0xFF1E1F22)

    val WhiteText = Color(0xFFFFFBE9)
    val LightGreyText = Color(0xFFC7C7C7)
    val ViridianText = Color(0xFF93F1E5)
    val DarkText = Color(0xFF4F5557)

    val BlackText = Color(0xFF27292A)

    val AccentAzure = Color(0xFF3D9BFF)
    val AccentAqua = Color(0xFF4CE0D2)
    val AccentRed = Color(0xFFE45A5A)
}

@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Subtle animated gradient used as the screen backdrop.
    val transition = rememberInfiniteTransition(label = "bg")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shift"
    )
    // Quantize the shift to reduce how often the gradient changes.
    // Increase `stepsPerUnit` for smoother motion (more updates);
    // decrease it for fewer updates (lower CPU/GPU cost).
    val stepsPerUnit = 10f
    val quantizedShift = (shift * stepsPerUnit).roundToInt() / stepsPerUnit
    val brush = Brush.linearGradient(
        colors = listOf(
            AppColors.Gradient1,
            AppColors.Gradient2,
            AppColors.Gradient3,
            AppColors.Gradient2,
            AppColors.Gradient1
        ),
        start = Offset(0f, 200f * quantizedShift),
        end = Offset(1200f, 1200f + 200f * quantizedShift)
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    ) {
        content()
    }
}
