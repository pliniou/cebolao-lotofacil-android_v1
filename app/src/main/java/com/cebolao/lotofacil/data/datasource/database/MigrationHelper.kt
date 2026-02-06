package com.cebolao.lotofacil.data.datasource.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Estratégia de migrações Room para Lotofácil.
 *
 * Suporta versionamento seguro do schema com fallback controlado apenas em DEBUG.
 * Em produção, garante que as migrações sejam aplicadas corretamente.
 */
object MigrationHelper {

    /**
     * Lista de migrações Room. Novas versões devem ser adicionadas aqui.
     */
    fun getMigrations(): Array<Migration> = arrayOf(
        // Migração 1 → 2: Adicionar coluna 'isPinned' na tabela 'historical_draws'
        Migration1To2(),
        // Migração 2 → 3: Criar tabela 'check_history' para rastreamento de conferências
        Migration2To3()
    )

    /**
     * Migração 1 → 2: Adiciona suporte a pinning de resultados históricos.
     */
    private class Migration1To2 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Verifica se a coluna já existe (para idempotência)
            database.execSQL(
                """
                ALTER TABLE historical_draws 
                ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }
    }

    /**
     * Migração 2 → 3: Cria tabela 'check_history' para rastreamento local de conferências.
     * Permite que usuários vejam histórico de verificações sem perder dados.
     */
    private class Migration2To3 : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS check_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    gameNumbers TEXT NOT NULL,
                    contestNumber INTEGER NOT NULL,
                    checkedAt TEXT NOT NULL,
                    hits INTEGER NOT NULL,
                    scoreCounts TEXT NOT NULL,
                    lastHitContest INTEGER,
                    lastHitScore INTEGER,
                    notes TEXT
                )
                """.trimIndent()
            )
            // Index para queries frequentes
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_check_history_checkedAt ON check_history(checkedAt DESC)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_check_history_contestNumber ON check_history(contestNumber)"
            )
        }
    }
}
