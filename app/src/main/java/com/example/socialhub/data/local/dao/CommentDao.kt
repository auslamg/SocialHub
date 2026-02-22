package com.example.socialhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.socialhub.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for comment table queries and mutations.
 */
@Dao
interface CommentDao {
    // All comments.
    @Query("SELECT * FROM comments")
    fun observeAll(): Flow<List<CommentEntity>>

    // Comments for a post.
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun observeComments(postId: Long): Flow<List<CommentEntity>>

    // Comments by user.
    @Query("SELECT * FROM comments WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeByUser(userId: Long): Flow<List<CommentEntity>>

    // Insert or replace a comment by primary key.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(comment: CommentEntity)

    // Bulk insert for comments.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(comments: List<CommentEntity>)

    // Update an existing comment row.
    @Update
    suspend fun update(comment: CommentEntity)

    // Delete a comment row.
    @Delete
    suspend fun delete(comment: CommentEntity)
}
