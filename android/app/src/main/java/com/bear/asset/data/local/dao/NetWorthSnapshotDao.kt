package com.bear.asset.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bear.asset.data.local.entity.NetWorthSnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetWorthSnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snapshot: NetWorthSnapshotEntity)

    @Query("SELECT * FROM net_worth_snapshot WHERE snapshotDate = :date LIMIT 1")
    suspend fun getSnapshotByDate(date: String): NetWorthSnapshotEntity?

    @Query("SELECT * FROM net_worth_snapshot ORDER BY snapshotDate DESC LIMIT 1")
    suspend fun getLatestSnapshot(): NetWorthSnapshotEntity?

    @Query("SELECT * FROM net_worth_snapshot WHERE snapshotDate >= :startDate ORDER BY snapshotDate ASC")
    fun getSnapshotsSince(startDate: String): Flow<List<NetWorthSnapshotEntity>>

    @Query("SELECT * FROM net_worth_snapshot ORDER BY snapshotDate DESC LIMIT :limit")
    suspend fun getRecentSnapshots(limit: Int): List<NetWorthSnapshotEntity>
}
