package com.example.socialhub.ui.screens.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.socialhub.ui.components.PostCard

@Composable
fun HubScreen() {
    AnimatedGradientBackground {
        Column(modifier = Modifier.padding(SocialHubScreenPadding())) {
            Text(
                text = "Hub",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = AppColors.Sand
            )
            Text(
                text = "Infinite feed, straight from your circle.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.Slate
            )
            Spacer(modifier = Modifier.height(16.dp))
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
        }
    }
}

private data class SamplePost(
    val author: String,
    val handle: String,
    val body: String,
    val stamp: String
)

private fun samplePosts(): List<SamplePost> = listOf(
    SamplePost("Iris Calder", "@iris", "Exploring quiet builds for loud ideas.", "2m"),
    SamplePost("Nova Park", "@novap", "Late-night sprint, early-morning glow.", "7m"),
    SamplePost("Theo Ames", "@theo", "Shipping, but with intention.", "11m"),
    SamplePost("Juno Vale", "@juno", "Sketching a timeline that breathes.", "18m"),
    SamplePost("Mara Knox", "@mara", "Anyone else craving offline-first vibes?", "23m"),
    SamplePost("Ezra Lux", "@ezral", "Built a new grid for saved thoughts.", "30m"),
    SamplePost("Asha Reed", "@asha", "Morning drafts become midnight posts.", "34m"),
    SamplePost("Kai North", "@kain", "Polishing the feed shimmer today.", "41m"),
    SamplePost("Sol Vega", "@sol", "I want better tools for tiny stories.", "48m"),
    SamplePost("Lena Quill", "@lena", "Design review: bold or bolder?", "1h")
)
