package com.example.socialhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.socialhub.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun observeTimeline(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun observePost(postId: Long): Flow<PostEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(posts: List<PostEntity>)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun delete(postId: Long)
}
