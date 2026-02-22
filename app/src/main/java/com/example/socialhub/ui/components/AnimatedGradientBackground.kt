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
import kotlin.math.roundToInt

/**
 * Animated gradient background used as the base for app screens.
 */
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
