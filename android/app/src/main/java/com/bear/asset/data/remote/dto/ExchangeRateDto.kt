package com.bear.asset.data.remote.dto

data class ExchangeRateDto(
    val date: String,
    val base: String,
    val quote: String,
    val rate: Double
)
