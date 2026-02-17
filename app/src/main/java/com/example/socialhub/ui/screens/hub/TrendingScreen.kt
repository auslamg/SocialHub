package com.example.socialhub.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun TrendingScreen() {
    AnimatedGradientBackground {
        Column(modifier = Modifier.padding(SocialHubScreenPadding())) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Trending",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = AppColors.Sand
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(AppColors.Accent, shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LIVE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = AppColors.Carbon
                    )
                }
            }
            Text(
                text = "Most-loved posts across the network.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.Slate
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(trendingPosts()) { post ->
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

private data class TrendingPost(
    val author: String,
    val handle: String,
    val body: String,
    val stamp: String
)

private fun trendingPosts(): List<TrendingPost> = listOf(
    TrendingPost("Atlas Jade", "@atlas", "Designing for kindness at scale.", "Trending"),
    TrendingPost("Rhea Noon", "@rhea", "Hot take: text-first feeds win.", "Trending"),
    TrendingPost("Cato Lin", "@cato", "Micro-posts, macro impact.", "Trending"),
    TrendingPost("Noor Hale", "@noor", "Community notes deserve better UI.", "Trending"),
    TrendingPost("Vega Street", "@vega", "Signal beats noise every time.", "Trending"),
    TrendingPost("Orion Park", "@orion", "Shipping the feed refresh in 3...", "Trending"),
    TrendingPost("Tess Arden", "@tess", "Love seeing thoughtfulness in threads.", "Trending"),
    TrendingPost("Milo Gray", "@milo", "Icons can wait, clarity cannot.", "Trending"),
    TrendingPost("Dara Finch", "@dara", "User trust is a product feature.", "Trending"),
    TrendingPost("Skye Reed", "@skye", "Building with empathy is a superpower.", "Trending")
)
