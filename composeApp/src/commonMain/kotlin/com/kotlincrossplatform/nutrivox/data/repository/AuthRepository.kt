package com.kotlincrossplatform.nutrivox.data.repository

import com.kotlincrossplatform.nutrivox.data.remote.ApiClient
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val email: String, val password: String, val fullName: String, val role: String)

@Serializable
data class TokenResponse(val accessToken: String, val refreshToken: String)

@Serializable
data class ApiWrapper<T>(val success: Boolean, val data: T? = null, val error: String? = null)

class AuthRepository(private val apiClient: ApiClient) {

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiClient.httpClient.post("/auth/login") {
                setBody(LoginRequest(email, password))
            }.body<ApiWrapper<TokenResponse>>()

            if (response.success && response.data != null) {
                // Decode JWT to get role and userId (simple base64 decode of payload)
                val payload = decodeJwtPayload(response.data.accessToken)
                TokenStorage.saveTokens(
                    access = response.data.accessToken,
                    refresh = response.data.refreshToken,
                    role = payload["role"] ?: "patient",
                    userId = payload["userId"] ?: ""
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, fullName: String, role: String): Result<Unit> {
        return try {
            val response = apiClient.httpClient.post("/auth/register") {
                setBody(RegisterRequest(email, password, fullName, role))
            }.body<ApiWrapper<Map<String, String>>>()

            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.error ?: "Registration failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        TokenStorage.clear()
    }

    private fun decodeJwtPayload(jwt: String): Map<String, String> {
        return try {
            val parts = jwt.split(".")
            if (parts.size != 3) return emptyMap()
            val payload = parts[1]
            // Pad base64 if needed
            val padded = payload + "=".repeat((4 - payload.length % 4) % 4)
            val decoded = kotlin.io.encoding.Base64.decode(padded).decodeToString()
            // Simple JSON parsing for claims
            val map = mutableMapOf<String, String>()
            decoded.removeSurrounding("{", "}").split(",").forEach { pair ->
                val (key, value) = pair.split(":", limit = 2).map { it.trim().removeSurrounding("\"") }
                map[key] = value
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
