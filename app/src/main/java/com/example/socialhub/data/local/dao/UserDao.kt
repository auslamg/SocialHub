package com.example.socialhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.socialhub.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun observeUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE username LIKE :query || '%' ORDER BY username ASC")
    fun searchByUsername(query: String): Flow<List<UserEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username LIMIT 1)")
    suspend fun existsUsername(username: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(users: List<UserEntity>)

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)
}
