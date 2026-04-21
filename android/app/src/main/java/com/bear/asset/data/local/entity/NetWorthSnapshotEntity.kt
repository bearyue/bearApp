package com.bear.asset.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "net_worth_snapshot")
data class NetWorthSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val snapshotDate: String,
    val totalAsset: Double,
    val totalLiability: Double,
    val netWorth: Double,
    val breakdownJson: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
