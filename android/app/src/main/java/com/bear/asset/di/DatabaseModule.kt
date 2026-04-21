package com.bear.asset.di

import android.content.Context
import androidx.room.Room
import com.bear.asset.data.local.AppDatabase
import com.bear.asset.data.local.dao.AssetDao
import com.bear.asset.data.local.dao.NetWorthSnapshotDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bear_asset_db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideAssetDao(database: AppDatabase): AssetDao {
        return database.assetDao()
    }

    @Provides
    fun provideNetWorthSnapshotDao(database: AppDatabase): NetWorthSnapshotDao {
        return database.netWorthSnapshotDao()
    }
}
