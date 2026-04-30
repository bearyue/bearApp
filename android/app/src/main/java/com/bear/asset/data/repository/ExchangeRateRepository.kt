package com.bear.asset.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bear.asset.data.remote.ExchangeRateService
import com.bear.asset.domain.model.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class ExchangeRateItem(
    val currency: Currency,
    val rate: Double
)

data class ExchangeRateSnapshot(
    val base: Currency,
    val rates: List<ExchangeRateItem>,
    val sourceDate: String,
    val cachedDate: String
) {
    fun convert(amount: Double, from: Currency, to: Currency): Double {
        if (from == to) return amount
        val fromRate = rateOf(from)
        val toRate = rateOf(to)
        if (fromRate <= 0.0 || toRate <= 0.0) return amount
        val baseAmount = amount / fromRate
        return baseAmount * toRate
    }

    private fun rateOf(currency: Currency): Double {
        if (currency == base) return 1.0
        return rates.firstOrNull { it.currency == currency }?.rate ?: 0.0
    }
}

@Singleton
class ExchangeRateRepository @Inject constructor(
    application: Application,
    private val service: ExchangeRateService
) {
    private val prefs: SharedPreferences = application.getSharedPreferences(
        "exchange_rate_cache",
        Context.MODE_PRIVATE
    )
    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val _rates = MutableStateFlow<ExchangeRateSnapshot?>(null)
    val rates: StateFlow<ExchangeRateSnapshot?> = _rates.asStateFlow()

    suspend fun getDailyRates(): ExchangeRateSnapshot {
        val today = dayFormat.format(Date())
        readCached(today)?.let {
            _rates.value = it
            return it
        }

        val remoteRates = service.getRates(
            base = Currency.USD.name,
            quotes = listOf(Currency.CNY.name, Currency.HKD.name).joinToString(",")
        )

        val sourceDate = remoteRates.firstOrNull()?.date ?: today
        val cny = remoteRates.firstOrNull { it.quote == Currency.CNY.name }?.rate
        val hkd = remoteRates.firstOrNull { it.quote == Currency.HKD.name }?.rate

        prefs.edit()
            .putString(KEY_CACHE_DATE, today)
            .putString(KEY_SOURCE_DATE, sourceDate)
            .putFloat(KEY_CNY_RATE, (cny ?: 0.0).toFloat())
            .putFloat(KEY_HKD_RATE, (hkd ?: 0.0).toFloat())
            .apply()

        return buildSnapshot(today, sourceDate, cny ?: 0.0, hkd ?: 0.0).also {
            _rates.value = it
        }
    }

    fun getCachedRates(): ExchangeRateSnapshot {
        val today = dayFormat.format(Date())
        return readCached(today) ?: defaultSnapshot(today)
    }

    private fun readCached(today: String): ExchangeRateSnapshot? {
        if (prefs.getString(KEY_CACHE_DATE, null) != today) return null
        val sourceDate = prefs.getString(KEY_SOURCE_DATE, null) ?: return null
        val cny = prefs.getFloat(KEY_CNY_RATE, 0f).toDouble()
        val hkd = prefs.getFloat(KEY_HKD_RATE, 0f).toDouble()
        if (cny <= 0.0 || hkd <= 0.0) return null
        return buildSnapshot(today, sourceDate, cny, hkd)
    }

    private fun buildSnapshot(
        cachedDate: String,
        sourceDate: String,
        cny: Double,
        hkd: Double
    ): ExchangeRateSnapshot {
        return ExchangeRateSnapshot(
            base = Currency.USD,
            sourceDate = sourceDate,
            cachedDate = cachedDate,
            rates = listOf(
                ExchangeRateItem(Currency.USD, 1.0),
                ExchangeRateItem(Currency.CNY, cny),
                ExchangeRateItem(Currency.HKD, hkd)
            )
        )
    }

    private fun defaultSnapshot(today: String): ExchangeRateSnapshot {
        return buildSnapshot(
            cachedDate = today,
            sourceDate = today,
            cny = 7.0,
            hkd = 7.8
        )
    }

    private companion object {
        const val KEY_CACHE_DATE = "cache_date"
        const val KEY_SOURCE_DATE = "source_date"
        const val KEY_CNY_RATE = "cny_rate"
        const val KEY_HKD_RATE = "hkd_rate"
    }
}
