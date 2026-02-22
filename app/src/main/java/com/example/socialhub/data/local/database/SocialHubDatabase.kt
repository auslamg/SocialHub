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

// Room database acts as a local, emulated backend for app data.
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
    // DAOs expose typed APIs for each table.
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
