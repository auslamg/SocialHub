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
    // Reactive stream of all users in the local database.
    @Query("SELECT * FROM users")
    fun observeUsers(): Flow<List<UserEntity>>

    // Observe a single user by primary key.
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: Long): Flow<UserEntity?>

    // Prefix search for usernames (used by Search screen).
    @Query("SELECT * FROM users WHERE username LIKE :query || '%' ORDER BY username ASC")
    fun searchByUsername(query: String): Flow<List<UserEntity>>

    // Used to enforce unique usernames when creating users.
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username LIMIT 1)")
    suspend fun existsUsername(username: String): Boolean

    // Create or replace a user by primary key.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    // Bulk insert for seed/demo data.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(users: List<UserEntity>)

    // Update an existing row.
    @Update
    suspend fun update(user: UserEntity)

    // Delete a row by entity.
    @Delete
    suspend fun delete(user: UserEntity)
}
