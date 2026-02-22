package com.example.socialhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a user profile.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val username: String,
    val name: String,
    val email: String?,
    val avatarUrl: String?,
    val bio: String?,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int
)
