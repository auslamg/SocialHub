package com.example.socialhub.ui.screens.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.PostDao
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.PostEntity
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.remote.api.PostApi
import com.example.socialhub.data.remote.api.UserApi
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
    private val postApi: PostApi,
    private val userDao: UserDao,
    private val userApi: UserApi
) : ViewModel() {
    private val isLoading = MutableStateFlow(true)
    private val fetchedUserIds = mutableSetOf<Long>()

    val uiState: StateFlow<HubUiState> = combine(
        postDao.observeTimeline(),
        userDao.observeUsers(),
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
                val remotePosts = postApi.getPosts(limit = 20)
                val now = System.currentTimeMillis()
                val entities = remotePosts.posts.mapIndexed { index, remote ->
                    PostEntity(
                        id = remote.id,
                        userId = remote.userId,
                        content = remote.body.trim(),
                        createdAt = now - (index * 60_000L),
                        updatedAt = null,
                        likeCount = 0,
                        commentCount = 0,
                        isDraft = false
                    )
                }
                postDao.upsertAll(entities)
                fetchUsers(remotePosts.posts.map { it.userId }.distinct())
            } catch (_: Exception) {
                // Network errors are non-fatal; cached posts still display.
            } finally {
                isLoading.value = false
            }
        }
    }

    private suspend fun fetchUsers(userIds: List<Long>) {
        for (userId in userIds) {
            if (!fetchedUserIds.add(userId)) {
                continue
            }
            try {
                val remote = userApi.getUser(userId)
                val user = UserEntity(
                    id = remote.id,
                    username = remote.username,
                    name = "${remote.firstName} ${remote.lastName}".trim(),
                    email = remote.email,
                    avatarUrl = "https://i.pravatar.cc/150?u=${remote.username}",
                    bio = buildBio(
                        city = remote.address.city,
                        country = remote.address.country,
                        university = remote.university,
                        companyName = remote.company.name
                    ),
                    followersCount = 0,
                    followingCount = 0,
                    postsCount = 0
                )
                userDao.upsert(user)
            } catch (_: Exception) {
                // Non-fatal; keep placeholders if a user lookup fails.
            }
        }
    }

    private fun buildBio(
        city: String,
        country: String,
        university: String,
        companyName: String
    ): String {
        val location = listOf(city.trim(), country.trim())
            .filter { it.isNotBlank() }
            .joinToString(", ")
        val lineOne = if (location.isNotBlank() && university.isNotBlank()) {
            "$location - ${university.trim()}"
        } else {
            listOf(location, university.trim()).filter { it.isNotBlank() }.joinToString(" ")
        }
        return listOf(lineOne, companyName.trim())
            .filter { it.isNotBlank() }
            .joinToString("\n")
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
