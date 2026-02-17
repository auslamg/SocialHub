package com.example.socialhub.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "likes",
    primaryKeys = ["postId", "userId"],
    indices = [Index("createdAt")]
)
data class LikeEntity(
    val postId: Long,
    val userId: Long,
    val createdAt: Long
)
