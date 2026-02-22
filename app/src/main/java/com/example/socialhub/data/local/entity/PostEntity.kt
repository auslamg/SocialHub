package com.example.socialhub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    indices = [Index("userId"), Index("createdAt")]
)
data class PostEntity(
    @PrimaryKey val id: Long,
    val userId: Long,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long?,
    val likeCount: Int,
    val dislikeCount: Int,
    val commentCount: Int,
    val isDraft: Boolean = false
)
