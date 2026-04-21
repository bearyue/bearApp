package com.bear.asset.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bear.asset.data.local.entity.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: AssetEntity): Long

    @Update
    suspend fun update(asset: AssetEntity)

    @Delete
    suspend fun delete(asset: AssetEntity)

    @Query("DELETE FROM asset WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM asset ORDER BY updatedAt DESC")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM asset WHERE category = :category ORDER BY updatedAt DESC")
    fun getAssetsByCategory(category: String): Flow<List<AssetEntity>>

    @Query("SELECT * FROM asset WHERE id = :id")
    suspend fun getAssetById(id: Long): AssetEntity?

    @Query("SELECT * FROM asset WHERE id = :id")
    fun getAssetByIdFlow(id: Long): Flow<AssetEntity?>

    @Query("SELECT category, SUM(amountCny) as total FROM asset GROUP BY category")
    suspend fun getCategorySums(): List<CategorySum>

    @Query("SELECT * FROM asset WHERE name LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchAssets(query: String): Flow<List<AssetEntity>>
}

data class CategorySum(
    val category: String,
    val total: Double
)
