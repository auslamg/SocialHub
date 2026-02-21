package com.example.socialhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.socialhub.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    // Full search history.
    @Query("SELECT * FROM search_history")
    fun observeAll(): Flow<List<SearchHistoryEntity>>

    // Recent entries for quick suggestions.
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC")
    fun observeRecent(): Flow<List<SearchHistoryEntity>>

    // Filter by query prefix.
    @Query("SELECT * FROM search_history WHERE query LIKE :query || '%' ORDER BY searchedAt DESC")
    fun observeByQuery(query: String): Flow<List<SearchHistoryEntity>>

    // Insert or replace a history entry.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: SearchHistoryEntity)

    // Update an existing entry.
    @Update
    suspend fun update(entry: SearchHistoryEntity)

    // Delete a single entry.
    @Delete
    suspend fun delete(entry: SearchHistoryEntity)

    // Clear all history.
    @Query("DELETE FROM search_history")
    suspend fun clear()
}
