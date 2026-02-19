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

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        LikeEntity::class,
        SearchHistoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SocialHubDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
