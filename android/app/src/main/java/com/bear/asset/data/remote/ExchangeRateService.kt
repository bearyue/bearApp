package com.bear.asset.data.remote

import com.bear.asset.data.remote.dto.ExchangeRateDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateService {

    @GET("v2/rates")
    suspend fun getRates(
        @Query("base") base: String,
        @Query("quotes") quotes: String
    ): List<ExchangeRateDto>
}
