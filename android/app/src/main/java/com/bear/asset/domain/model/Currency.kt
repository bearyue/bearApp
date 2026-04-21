package com.bear.asset.domain.model

enum class Currency(val displayName: String, val symbol: String) {
    CNY("人民币", "¥"),
    HKD("港币", "HK$"),
    USD("美元", "$")
}
