package com.bear.asset.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bear.asset.data.local.entity.AssetEntity
import com.bear.asset.data.repository.AssetRepository
import com.bear.asset.data.repository.ExchangeRateRepository
import com.bear.asset.data.repository.ExchangeRateSnapshot
import com.bear.asset.domain.model.AssetCategory
import com.bear.asset.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryGroup(
    val category: AssetCategory,
    val assets: List<AssetEntity>,
    val totalAmount: Double,
    val isExpanded: Boolean = true
)

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val repository: AssetRepository,
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _expandedCategories = MutableStateFlow(AssetCategory.entries.toSet())
    private val initialRates = exchangeRateRepository.getCachedRates()

    init {
        viewModelScope.launch {
            runCatching { exchangeRateRepository.getDailyRates() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val assets = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllAssets()
            } else {
                repository.searchAssets(query)
            }
        }

    val categoryGroups: StateFlow<List<CategoryGroup>> = assets
        .combine(_expandedCategories) { assets, expanded -> assets to expanded }
        .combine(exchangeRateRepository.rates) { (assets, expanded), latestRates ->
            val exchangeRates = latestRates ?: initialRates
            AssetCategory.entries.mapNotNull { category ->
                val categoryAssets = assets.filter { it.category == category.name }
                if (categoryAssets.isEmpty()) return@mapNotNull null
                CategoryGroup(
                    category = category,
                    assets = categoryAssets,
                    totalAmount = categoryAssets.sumOf { asset ->
                        val currency = runCatching { Currency.valueOf(asset.currency) }.getOrDefault(Currency.CNY)
                        exchangeRates.convert(asset.amount, currency, Currency.CNY)
                    },
                    isExpanded = expanded.contains(category)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exchangeRates: StateFlow<ExchangeRateSnapshot?> = exchangeRateRepository.rates

    val isEmpty: StateFlow<Boolean> = categoryGroups
        .map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun convertAmount(amount: Double, from: Currency, to: Currency): Double {
        return (exchangeRates.value ?: initialRates).convert(amount, from, to)
    }

    fun toggleCategory(category: AssetCategory) {
        val current = _expandedCategories.value.toMutableSet()
        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }
        _expandedCategories.value = current
    }

    fun deleteAsset(asset: AssetEntity) {
        viewModelScope.launch {
            repository.deleteAsset(asset)
        }
    }
}
