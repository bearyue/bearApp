package com.bear.asset.data.remote

import retrofit2.http.GET

interface ApiService {

    @GET("/api/health")
    suspend fun healthCheck(): retrofit2.Response<Map<String, Any>>
}
