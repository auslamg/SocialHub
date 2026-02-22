package com.example.socialhub.ui.screens.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.repository.PostRepository
import com.example.socialhub.data.repository.UserRepository
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
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val isLoading = MutableStateFlow(true)

    val uiState: StateFlow<HubUiState> = combine(
        postRepository.observeTimeline(),
        userRepository.observeUsers(),
        isLoading
    ) { posts, users, loading ->
        val userMap = users.associateBy { it.id }
        HubUiState(
            posts = posts.take(20).map { post ->
                val user = userMap[post.userId]
                HubPost(
                    author = user?.name ?: "User ${post.userId}",
                    handle = user?.username?.let { "@$it" } ?: "@user${post.userId}",
                    avatarUrl = user?.avatarUrl,
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
                val entities = postRepository.refreshPosts(limit = 20)
                userRepository.fetchUsersByIds(entities.map { it.userId }.distinct())
            } catch (_: Exception) {
                // Network errors are non-fatal; cached posts still display.
            } finally {
                isLoading.value = false
            }
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
    val avatarUrl: String?,
    val body: String,
    val stamp: String
)
