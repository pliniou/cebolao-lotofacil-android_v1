package com.cebolao.lotofacil.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cebolao.lotofacil.data.datasource.database.entity.Converters
import com.cebolao.lotofacil.data.datasource.database.entity.HistoricalDrawEntity

@Database(entities = [HistoricalDrawEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LotofacilDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
