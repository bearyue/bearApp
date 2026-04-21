package com.bear.asset.data.repository

import com.bear.asset.data.local.dao.AssetDao
import com.bear.asset.data.local.dao.CategorySum
import com.bear.asset.data.local.dao.NetWorthSnapshotDao
import com.bear.asset.data.local.entity.AssetEntity
import com.bear.asset.data.local.entity.NetWorthSnapshotEntity
import com.bear.asset.domain.model.AssetCategory
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class NetWorthSummary(
    val totalAsset: Double,
    val totalLiability: Double,
    val netWorth: Double,
    val categorySums: Map<String, Double>
)

data class CategorySummary(
    val category: AssetCategory,
    val totalAmount: Double,
    val assetCount: Int
)

@Singleton
class AssetRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val snapshotDao: NetWorthSnapshotDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val gson = Gson()

    fun getAllAssets(): Flow<List<AssetEntity>> = assetDao.getAllAssets()

    fun getAssetsByCategory(category: String): Flow<List<AssetEntity>> =
        assetDao.getAssetsByCategory(category)

    suspend fun getAssetById(id: Long): AssetEntity? = assetDao.getAssetById(id)

    fun getAssetByIdFlow(id: Long): Flow<AssetEntity?> = assetDao.getAssetByIdFlow(id)

    fun searchAssets(query: String): Flow<List<AssetEntity>> = assetDao.searchAssets(query)

    suspend fun insertAsset(asset: AssetEntity): Long = assetDao.insert(asset)

    suspend fun updateAsset(asset: AssetEntity) = assetDao.update(asset)

    suspend fun deleteAsset(asset: AssetEntity) = assetDao.delete(asset)

    suspend fun deleteAssetById(id: Long) = assetDao.deleteById(id)

    suspend fun getNetWorthSummary(): NetWorthSummary {
        val categorySums = assetDao.getCategorySums()
        val sumsMap = categorySums.associate { it.category to it.total }

        var totalAsset = 0.0
        var totalLiability = 0.0

        sumsMap.forEach { (category, total) ->
            if (category == AssetCategory.LIABILITY.name) {
                totalLiability += total
            } else {
                totalAsset += total
            }
        }

        return NetWorthSummary(
            totalAsset = totalAsset,
            totalLiability = totalLiability,
            netWorth = totalAsset - totalLiability,
            categorySums = sumsMap
        )
    }

    suspend fun getCategorySummaries(assets: List<AssetEntity>): List<CategorySummary> {
        return AssetCategory.entries.mapNotNull { category ->
            val categoryAssets = assets.filter { it.category == category.name }
            if (categoryAssets.isEmpty()) return@mapNotNull null
            CategorySummary(
                category = category,
                totalAmount = categoryAssets.sumOf { it.amountCny },
                assetCount = categoryAssets.size
            )
        }
    }

    suspend fun saveSnapshot() {
        val today = dateFormat.format(Date())
        val existing = snapshotDao.getSnapshotByDate(today)
        val summary = getNetWorthSummary()

        val snapshot = NetWorthSnapshotEntity(
            id = existing?.id ?: 0,
            snapshotDate = today,
            totalAsset = summary.totalAsset,
            totalLiability = summary.totalLiability,
            netWorth = summary.netWorth,
            breakdownJson = gson.toJson(summary.categorySums)
        )
        snapshotDao.insert(snapshot)
    }

    suspend fun getYesterdaySnapshot(): NetWorthSnapshotEntity? {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(cal.time)
        return snapshotDao.getSnapshotByDate(yesterday)
    }

    suspend fun getTodaySnapshot(): NetWorthSnapshotEntity? {
        val today = dateFormat.format(Date())
        return snapshotDao.getSnapshotByDate(today)
    }

    suspend fun getRecentSnapshots(limit: Int): List<NetWorthSnapshotEntity> =
        snapshotDao.getRecentSnapshots(limit)

    fun getSnapshotsSince(startDate: String): Flow<List<NetWorthSnapshotEntity>> =
        snapshotDao.getSnapshotsSince(startDate)
}
