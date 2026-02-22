package com.example.socialhub.data.remote.dto

data class RemotePostsResponse(
    val posts: List<RemotePostDto>
)

data class RemotePostDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String,
    val reactions: RemotePostReactionsDto
)

data class RemotePostReactionsDto(
    val likes: Int,
    val dislikes: Int
)
