package com.example.socialhub.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.socialhub.data.local.dao.CommentDao
import com.example.socialhub.data.local.dao.LikeDao
import com.example.socialhub.data.local.dao.PostDao
import com.example.socialhub.data.local.dao.SearchHistoryDao
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.CommentEntity
import com.example.socialhub.data.local.entity.LikeEntity
import com.example.socialhub.data.local.entity.PostEntity
import com.example.socialhub.data.local.entity.SearchHistoryEntity
import com.example.socialhub.data.local.entity.UserEntity

/**
 * Room database that acts as the local backend for SocialHub.
 */
@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        LikeEntity::class,
        SearchHistoryEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class SocialHubDatabase : RoomDatabase() {
    /**
     * DAO for user table operations.
     */
    abstract fun userDao(): UserDao
    /**
     * DAO for post table operations.
     */
    abstract fun postDao(): PostDao
    /**
     * DAO for comment table operations.
     */
    abstract fun commentDao(): CommentDao
    /**
     * DAO for like table operations.
     */
    abstract fun likeDao(): LikeDao
    /**
     * DAO for search history operations.
     */
    abstract fun searchHistoryDao(): SearchHistoryDao
}
