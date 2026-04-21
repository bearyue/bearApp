package com.bear.asset.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bear.asset.data.repository.AssetRepository
import com.bear.asset.data.repository.CategorySummary
import com.bear.asset.domain.model.AssetCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val netWorth: Double = 0.0,
    val totalAsset: Double = 0.0,
    val totalLiability: Double = 0.0,
    val dailyChange: Double = 0.0,
    val dailyChangePercent: Double = 0.0,
    val categorySummaries: List<CategorySummary> = emptyList(),
    val isLoading: Boolean = true,
    val snapshotSavedToday: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val summary = repository.getNetWorthSummary()
                val allAssets = repository.getAllAssets().first()
                val catSummaries = repository.getCategorySummaries(allAssets)

                // Calculate daily change
                val yesterday = repository.getYesterdaySnapshot()
                val dailyChange = if (yesterday != null) {
                    summary.netWorth - yesterday.netWorth
                } else 0.0
                val dailyChangePercent = if (yesterday != null && yesterday.netWorth != 0.0) {
                    (dailyChange / yesterday.netWorth) * 100
                } else 0.0

                _uiState.value = HomeUiState(
                    netWorth = summary.netWorth,
                    totalAsset = summary.totalAsset,
                    totalLiability = summary.totalLiability,
                    dailyChange = dailyChange,
                    dailyChangePercent = dailyChangePercent,
                    categorySummaries = catSummaries,
                    isLoading = false
                )

                // Auto-save today's snapshot
                val todaySnapshot = repository.getTodaySnapshot()
                if (todaySnapshot == null && allAssets.isNotEmpty()) {
                    repository.saveSnapshot()
                    _uiState.value = _uiState.value.copy(snapshotSavedToday = true)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
