package com.example.socialhub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.socialhub.R

@Composable
fun PostCard(
    author: String,
    handle: String,
    body: String,
    likeCount: Int = 0,
    dislikeCount: Int = 0,
    stamp: String,
    avatarUrl: String? = null,
    onHandleClick: (() -> Unit)? = null,
    showEdit: Boolean = false,
    onEditClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    PostCardFrame(
        author = author,
        handle = handle,
        stamp = stamp,
        avatarUrl = avatarUrl,
        likeCount = likeCount,
        dislikeCount = dislikeCount,
        onHandleClick = onHandleClick,
        showEdit = showEdit,
        onEditClick = onEditClick,
        modifier = modifier
    ) {
        Text(
            text = body,
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = AppColors.WhiteText
        )
    }
}

@Composable
fun CreatePostCard(
    author: String,
    handle: String,
    stamp: String,
    avatarUrl: String? = null,
    content: String,
    onContentChange: (String) -> Unit,
    supportingText: String? = null,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    PostCardFrame(
        author = author,
        handle = handle,
        stamp = stamp,
        avatarUrl = avatarUrl,
        likeCount = 0,
        dislikeCount = 0,
        onHandleClick = null,
        showEdit = false,
        onEditClick = null,
        modifier = modifier
    ) {
        DarkOutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            label = "What's happening?",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            outlineColor = AppColors.AccentAqua,
            supportingText = supportingText,
            isError = isError
        )
    }
}

@Composable
private fun PostCardFrame(
    author: String,
    handle: String,
    stamp: String,
    avatarUrl: String?,
    likeCount: Int,
    dislikeCount: Int,
    onHandleClick: (() -> Unit)?,
    showEdit: Boolean,
    onEditClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    bodyContent: @Composable () -> Unit
) {
    // Reusable feed card for posts and post creation.
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = AppColors.PostCardBG
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AppColors.AccentAqua)
                ) {
                    if (!avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "$author avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.avatar_placeholder),
                            error = painterResource(R.drawable.avatar_placeholder)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.avatar_placeholder),
                            contentDescription = "$author avatar placeholder",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    val handleModifier = if (onHandleClick != null) {
                        Modifier.clickable { onHandleClick() }
                    } else {
                        Modifier
                    }
                    Text(
                        text = author,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = AppColors.WhiteText
                    )
                    Text(
                        text = handle,
                        modifier = handleModifier,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = AppColors.ViridianText
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stamp,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = AppColors.LightGreyText
                )
                if (showEdit) {
                    Spacer(modifier = Modifier.size(8.dp))
                    IconButton(onClick = { onEditClick?.invoke() }) {
                        Image(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "Edit post",
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(AppColors.WhiteText)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            bodyContent()
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(" ", fontFamily = FontFamily.Monospace, color = AppColors.LightGreyText)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_thumbs_up),
                        contentDescription = "Like",
                        modifier = Modifier.size(14.dp),
                        colorFilter = ColorFilter.tint(AppColors.WhiteText)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = likeCount.toString(),
                        fontFamily = FontFamily.Monospace,
                        color = AppColors.LightGreyText
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_thumbs_down),
                        contentDescription = "Dislike",
                        modifier = Modifier.size(14.dp),
                        colorFilter = ColorFilter.tint(AppColors.WhiteText)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = dislikeCount.toString(),
                        fontFamily = FontFamily.Monospace,
                        color = AppColors.LightGreyText
                    )
                }
            }
        }
    }
}
