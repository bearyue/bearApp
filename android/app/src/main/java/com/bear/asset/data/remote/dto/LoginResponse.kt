package com.bear.asset.data.remote.dto

data class LoginResponse(
    val token: String,
    val userId: Long,
    val username: String,
    val nickname: String,
    val role: String
)
