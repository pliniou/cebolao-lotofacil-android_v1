package com.cebolao.lotofacil.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cebolao.lotofacil.data.datasource.database.entity.HistoricalDrawEntity

@Dao
interface HistoryDao {

    @Query("SELECT * FROM historical_draws ORDER BY contestNumber DESC")
    suspend fun getAll(): List<HistoricalDrawEntity>

    @Upsert
    suspend fun upsertAll(draws: List<HistoricalDrawEntity>)

    // Insert only if not exists (for initial bulk load optimization if needed) but Upsert is generally safer
    // Replaced by upsertAll for consistency

    
    @Query("SELECT COUNT(*) FROM historical_draws")
    suspend fun getCount(): Int

    @Query("SELECT * FROM historical_draws WHERE contestNumber = :contestNumber")
    suspend fun getDraw(contestNumber: Int): HistoricalDrawEntity?
    
    @Query("DELETE FROM historical_draws")
    suspend fun clearAll()
}
