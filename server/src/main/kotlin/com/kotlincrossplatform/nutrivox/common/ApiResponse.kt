package com.kotlincrossplatform.nutrivox.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
) {
    companion object {
        fun <T> ok(data: T) = ApiResponse(success = true, data = data)
        fun error(message: String) = ApiResponse<Unit>(success = false, error = message)
    }
}
