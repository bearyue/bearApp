package com.bear.asset.data.remote.dto

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    val isSuccess: Boolean get() = code == 200
}
