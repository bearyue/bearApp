package com.bear.asset.data.repository

import com.bear.asset.data.local.TokenManager
import com.bear.asset.data.remote.ApiService
import com.bear.asset.data.remote.dto.LoginRequest
import com.bear.asset.data.remote.dto.LoginResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccess && response.data != null) {
                val data = response.data
                tokenManager.saveLoginInfo(
                    token = data.token,
                    userId = data.userId,
                    username = data.username,
                    nickname = data.nickname
                )
                Result.success(data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun getCurrentToken(): String? = tokenManager.getToken()
}
