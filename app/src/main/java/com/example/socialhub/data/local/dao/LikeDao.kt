package com.example.socialhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.socialhub.data.local.entity.LikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {
    // All likes.
    @Query("SELECT * FROM likes")
    fun observeAll(): Flow<List<LikeEntity>>

    // Likes for a given post.
    @Query("SELECT * FROM likes WHERE postId = :postId")
    fun observeLikes(postId: Long): Flow<List<LikeEntity>>

    // Likes by user.
    @Query("SELECT * FROM likes WHERE userId = :userId")
    fun observeByUser(userId: Long): Flow<List<LikeEntity>>

    // Insert or replace a like row.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(like: LikeEntity)

    // Update a like row.
    @Update
    suspend fun update(like: LikeEntity)

    // Delete a like row.
    @Delete
    suspend fun delete(like: LikeEntity)

    // Delete a like by composite key.
    @Query("DELETE FROM likes WHERE postId = :postId AND userId = :userId")
    suspend fun delete(postId: Long, userId: Long)
}
