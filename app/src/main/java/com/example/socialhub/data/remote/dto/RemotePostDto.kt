package com.example.socialhub.data.remote.dto

/**
 * Response wrapper for post list endpoints.
 */
data class RemotePostsResponse(
    val posts: List<RemotePostDto>
)

/**
 * Post payload returned by the remote API.
 */
data class RemotePostDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String,
    val reactions: RemotePostReactionsDto
)

/**
 * Reaction counts for a remote post.
 */
data class RemotePostReactionsDto(
    val likes: Int,
    val dislikes: Int
)
