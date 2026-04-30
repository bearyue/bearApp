package com.bear.asset.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bear.asset.data.repository.AssetRepository
import com.bear.asset.data.repository.CategorySummary
import com.bear.asset.data.repository.ExchangeRateRepository
import com.bear.asset.domain.model.AssetCategory
import com.bear.asset.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
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
    private val repository: AssetRepository,
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    fun observeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val initialRates = exchangeRateRepository.getCachedRates()
                launch {
                    runCatching { exchangeRateRepository.getDailyRates() }
                }
                repository.getAllAssets()
                    .combine(exchangeRateRepository.rates) { allAssets, latestRates ->
                        allAssets to (latestRates ?: initialRates)
                    }
                    .collectLatest { (allAssets, exchangeRates) ->
                        val totalLiability = allAssets
                            .filter { it.category == AssetCategory.LIABILITY.name }
                            .sumOf { asset ->
                                val currency = runCatching { Currency.valueOf(asset.currency) }.getOrDefault(Currency.CNY)
                                exchangeRates.convert(asset.amount, currency, Currency.CNY)
                            }
                        val totalAsset = allAssets
                            .filter { it.category != AssetCategory.LIABILITY.name }
                            .sumOf { asset ->
                                val currency = runCatching { Currency.valueOf(asset.currency) }.getOrDefault(Currency.CNY)
                                exchangeRates.convert(asset.amount, currency, Currency.CNY)
                            }
                        val netWorth = totalAsset - totalLiability
                        val catSummaries = AssetCategory.entries.mapNotNull { category ->
                            val categoryAssets = allAssets.filter { it.category == category.name }
                            if (categoryAssets.isEmpty()) return@mapNotNull null
                            CategorySummary(
                                category = category,
                                totalAmount = categoryAssets.sumOf { asset ->
                                    val currency = runCatching { Currency.valueOf(asset.currency) }.getOrDefault(Currency.CNY)
                                    exchangeRates.convert(asset.amount, currency, Currency.CNY)
                                },
                                assetCount = categoryAssets.size
                            )
                        }

                        val yesterday = repository.getYesterdaySnapshot()
                        val dailyChange = if (yesterday != null) netWorth - yesterday.netWorth else 0.0
                        val dailyChangePercent = if (yesterday != null && yesterday.netWorth != 0.0) {
                            (dailyChange / yesterday.netWorth) * 100
                        } else {
                            0.0
                        }

                        if (allAssets.isNotEmpty()) {
                            repository.saveSnapshot()
                        }

                        _uiState.value = HomeUiState(
                            netWorth = netWorth,
                            totalAsset = totalAsset,
                            totalLiability = totalLiability,
                            dailyChange = dailyChange,
                            dailyChangePercent = dailyChangePercent,
                            categorySummaries = catSummaries,
                            isLoading = false,
                            snapshotSavedToday = allAssets.isNotEmpty()
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
