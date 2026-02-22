package com.example.socialhub.ui.screens.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.session.CurrentUserStore
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

/**
 * Orchestrates the Hub feed state by combining timeline posts with user details.
 *
 * Responsibilities:
 * - Refresh the timeline on entry (network + Room).
 * - Resolve post authors from cached users (Room) for display.
 * - Expose a stable `StateFlow` for Compose to collect.
 *
 * Data flow:
 * - `PostRepository.observeTimeline()` emits whenever posts change in Room.
 * - `UserRepository.observeUsers()` emits whenever user rows change in Room.
 * - `combine` merges both streams and formats UI models.
 */
@HiltViewModel
class HubViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val currentUserStore: CurrentUserStore
) : ViewModel() {
    // Local loading flag surfaced to the UI alongside the list.
    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    /**
     * Stream of Hub UI state.
     *
     * Notes:
     * - The list is capped at 20 to match the current feed contract.
     * - User data is optional; placeholders are used while user lookups complete.
     */
    val uiState: StateFlow<HubUiState> = combine(
        postRepository.observeTimeline(),
        userRepository.observeUsers(),
        currentUserStore.currentUserId,
        isLoading,
        errorMessage
    ) { posts, users, currentUserId, loading, error ->
        // Build a fast lookup table to avoid O(n^2) matching during mapping.
        val userMap = users.associateBy { it.id }
        HubUiState(
            posts = posts.take(20).map { post ->
                // Resolve author metadata from the cache; fall back to placeholders.
                val user = userMap[post.userId]
                val isOwner = currentUserId != null && post.userId == currentUserId
                HubPost(
                    id = post.id,
                    userId = post.userId,
                    author = user?.name ?: "User ${post.userId}",
                    handle = user?.username?.let { "@$it" } ?: "@user${post.userId}",
                    avatarUrl = user?.avatarUrl,
                    body = post.content,
                    likeCount = post.likeCount,
                    dislikeCount = post.dislikeCount,
                    stamp = formatStamp(post.createdAt),
                    isOwner = isOwner
                )
            },
            isLoading = loading,
            errorMessage = error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HubUiState(isLoading = true)
    )

    init {
        // Kick off the initial refresh when the ViewModel is created.
        fetchPosts()
    }

    /**
     * Refreshes posts from the remote source, persists to Room, then fetches users.
     *
     * The user fetch is based on distinct userIds present in the refreshed posts.
     * Errors are swallowed so the UI can continue to render cached data.
     */
    private fun fetchPosts() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                // Refresh the timeline and then backfill user details used by the feed.
                val entities = postRepository.refreshPosts(limit = 20)
                userRepository.fetchUsersByIds(entities.map { it.userId }.distinct())
            } catch (error: Exception) {
                // Network errors are non-fatal; cached posts still display.
                errorMessage.value = error.message ?: "Couldn't load posts. Check your connection."
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Converts a post timestamp to a compact relative label for the feed.
     */
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

/**
 * Immutable UI model consumed by the Hub screen.
 */
data class HubUiState(
    val posts: List<HubPost> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Lightweight view model for a single feed card.
 */
data class HubPost(
    val id: Long,
    val userId: Long,
    val author: String,
    val handle: String,
    val avatarUrl: String?,
    val body: String,
    val likeCount: Int,
    val dislikeCount: Int,
    val stamp: String,
    val isOwner: Boolean
)
