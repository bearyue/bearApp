package com.bear.asset.ui.util

import com.bear.asset.domain.model.Currency
import java.text.DecimalFormat

object NumberFormatter {

    private val currencyFormat = DecimalFormat("#,##0.00")
    private val integerFormat = DecimalFormat("#,##0")
    private val percentFormat = DecimalFormat("0.00")

    fun formatCurrency(amount: Double, currency: Currency = Currency.CNY): String {
        return "${currency.symbol}${currencyFormat.format(amount)}"
    }

    fun formatCurrencyByCode(amount: Double, currencyCode: String): String {
        val currency = try {
            Currency.valueOf(currencyCode)
        } catch (_: Exception) {
            Currency.CNY
        }
        return formatCurrency(amount, currency)
    }

    fun formatWithSign(amount: Double, currency: Currency = Currency.CNY): String {
        val prefix = if (amount >= 0) "+" else ""
        return "$prefix${currency.symbol}${currencyFormat.format(amount)}"
    }

    fun formatPercent(value: Double): String {
        val prefix = if (value >= 0) "+" else ""
        return "$prefix${percentFormat.format(value)}%"
    }

    fun formatAbbreviated(amount: Double, currency: Currency = Currency.CNY): String {
        return when {
            amount >= 100_000_000 || amount <= -100_000_000 -> {
                val yi = amount / 100_000_000
                "${currency.symbol}${percentFormat.format(yi)}亿"
            }
            amount >= 10_000 || amount <= -10_000 -> {
                val wan = amount / 10_000
                "${currency.symbol}${percentFormat.format(wan)}万"
            }
            else -> formatCurrency(amount, currency)
        }
    }

    fun formatAmount(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun formatInteger(amount: Double): String {
        return integerFormat.format(amount)
    }
}
