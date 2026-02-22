package com.example.socialhub.ui.screens.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.PostDao
import com.example.socialhub.data.local.entity.PostEntity
import com.example.socialhub.data.remote.api.PostApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HubViewModel @Inject constructor(
    private val postDao: PostDao,
    private val postApi: PostApi
) : ViewModel() {
    private val isLoading = MutableStateFlow(true)

    val uiState: StateFlow<HubUiState> = combine(
        postDao.observeTimeline(),
        isLoading
    ) { posts, loading ->
        HubUiState(
            posts = posts.take(20).map { post ->
                HubPost(
                    author = "User ${post.userId}",
                    handle = "@user${post.userId}",
                    body = post.content,
                    stamp = formatStamp(post.createdAt)
                )
            },
            isLoading = loading
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HubUiState(isLoading = true)
    )

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val remotePosts = postApi.getPosts(limit = 20)
                val now = System.currentTimeMillis()
                val entities = remotePosts.mapIndexed { index, remote ->
                    PostEntity(
                        id = remote.id,
                        userId = remote.userId,
                        content = buildContent(remote.title, remote.body),
                        createdAt = now - (index * 60_000L),
                        updatedAt = null,
                        likeCount = 0,
                        commentCount = 0,
                        isDraft = false
                    )
                }
                postDao.upsertAll(entities)
            } catch (_: Exception) {
                // Network errors are non-fatal; cached posts still display.
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun buildContent(title: String, body: String): String {
        val trimmedTitle = title.trim()
        val trimmedBody = body.trim()
        return if (trimmedTitle.isNotBlank()) {
            "$trimmedTitle\n\n$trimmedBody"
        } else {
            trimmedBody
        }
    }

    private fun formatStamp(createdAt: Long): String {
        val now = System.currentTimeMillis()
        val minutes = ((now - createdAt) / 60_000L).coerceAtLeast(0)
        return when {
            minutes < 1 -> "now"
            minutes < 60 -> "${minutes}m"
            minutes < 1_440 -> "${minutes / 60}h"
            else -> "${minutes / 1_440}d"
        }
    }
}

data class HubUiState(
    val posts: List<HubPost> = emptyList(),
    val isLoading: Boolean = false
)

data class HubPost(
    val author: String,
    val handle: String,
    val body: String,
    val stamp: String
)
