package com.cebolao.lotofacil.di

import android.content.Context
import androidx.room.Room
import com.cebolao.lotofacil.data.datasource.database.CheckHistoryDao
import com.cebolao.lotofacil.data.datasource.database.HistoryDao
import com.cebolao.lotofacil.data.datasource.database.LotofacilDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLotofacilDatabase(
        @ApplicationContext context: Context
    ): LotofacilDatabase {
        return Room.databaseBuilder(
            context,
            LotofacilDatabase::class.java,
            "lotofacil_db"
        )
            // Projeto ainda não está em produção: prioriza inicialização estável
            // e evita bloqueios por migrações durante iteração.
            .fallbackToDestructiveMigration()
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(database: LotofacilDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideCheckHistoryDao(database: LotofacilDatabase): CheckHistoryDao {
        return database.checkHistoryDao()
    }
}
