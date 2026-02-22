package com.example.socialhub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a comment on a post.
 */
@Entity(
    tableName = "comments",
    indices = [Index("postId"), Index("userId"), Index("createdAt")]
)
data class CommentEntity(
    @PrimaryKey val id: Long,
    val postId: Long,
    val userId: Long,
    val content: String,
    val createdAt: Long
)
