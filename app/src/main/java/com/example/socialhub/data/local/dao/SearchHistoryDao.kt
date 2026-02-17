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
    @Query("SELECT * FROM search_history")
    fun observeAll(): Flow<List<SearchHistoryEntity>>

    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC")
    fun observeRecent(): Flow<List<SearchHistoryEntity>>

    @Query("SELECT * FROM search_history WHERE query LIKE :query || '%' ORDER BY searchedAt DESC")
    fun observeByQuery(query: String): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: SearchHistoryEntity)

    @Update
    suspend fun update(entry: SearchHistoryEntity)

    @Delete
    suspend fun delete(entry: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clear()
}
