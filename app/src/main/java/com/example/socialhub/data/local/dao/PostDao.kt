package com.example.socialhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.socialhub.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // Timeline posts for the home feed.
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun observeTimeline(): Flow<List<PostEntity>>

    // Observe all posts.
    @Query("SELECT * FROM posts")
    fun observeAll(): Flow<List<PostEntity>>

    // Observe a single post.
    @Query("SELECT * FROM posts WHERE id = :postId")
    fun observePost(postId: Long): Flow<PostEntity?>

    // Posts for a user profile.
    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeByUser(userId: Long): Flow<List<PostEntity>>

    // Upsert by id (insert or replace).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(post: PostEntity)

    // Bulk insert for posts.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(posts: List<PostEntity>)

    // Update an existing post row.
    @Update
    suspend fun update(post: PostEntity)

    // Delete a post row.
    @Delete
    suspend fun delete(post: PostEntity)

    // Delete by primary key without loading the entity.
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun delete(postId: Long)
}
