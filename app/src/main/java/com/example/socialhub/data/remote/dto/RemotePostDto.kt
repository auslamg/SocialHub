package com.example.socialhub.data.remote.dto

data class RemotePostDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String
)
