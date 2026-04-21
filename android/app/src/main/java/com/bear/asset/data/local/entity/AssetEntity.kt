package com.bear.asset.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asset")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val subType: String,
    val name: String,
    val currency: String = "CNY",
    val amount: Double = 0.0,
    val amountCny: Double = 0.0,
    val cost: Double? = null,
    val quantity: Double? = null,
    val code: String? = null,
    val extraJson: String? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
