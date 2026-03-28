package com.bear.asset.data.remote

import com.bear.asset.data.remote.dto.ApiResponse
import com.bear.asset.data.remote.dto.LoginRequest
import com.bear.asset.data.remote.dto.LoginResponse
import com.bear.asset.data.remote.dto.UserInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("/api/health")
    suspend fun healthCheck(): retrofit2.Response<Map<String, Any>>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("/api/user/me")
    suspend fun getUserInfo(): ApiResponse<UserInfo>
}
