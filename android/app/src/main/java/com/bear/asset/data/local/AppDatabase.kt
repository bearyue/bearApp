package com.bear.asset.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bear.asset.data.local.dao.AssetDao
import com.bear.asset.data.local.dao.NetWorthSnapshotDao
import com.bear.asset.data.local.entity.AssetEntity
import com.bear.asset.data.local.entity.NetWorthSnapshotEntity

@Database(
    entities = [AssetEntity::class, NetWorthSnapshotEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun assetDao(): AssetDao
    abstract fun netWorthSnapshotDao(): NetWorthSnapshotDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `asset` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `category` TEXT NOT NULL,
                        `subType` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `currency` TEXT NOT NULL DEFAULT 'CNY',
                        `amount` REAL NOT NULL DEFAULT 0.0,
                        `amountCny` REAL NOT NULL DEFAULT 0.0,
                        `cost` REAL,
                        `quantity` REAL,
                        `code` TEXT,
                        `extraJson` TEXT,
                        `note` TEXT,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `net_worth_snapshot` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `snapshotDate` TEXT NOT NULL,
                        `totalAsset` REAL NOT NULL,
                        `totalLiability` REAL NOT NULL,
                        `netWorth` REAL NOT NULL,
                        `breakdownJson` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
