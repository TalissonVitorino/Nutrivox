package com.kotlincrossplatform.nutrivox.data.repository

import com.kotlincrossplatform.nutrivox.data.remote.ApiClient
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String,
    val phone: String? = null,
    val professionalRegistration: String? = null
)

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
                val payload = decodeJwtPayload(response.data.accessToken)
                TokenStorage.saveTokens(
                    access = response.data.accessToken,
                    refresh = response.data.refreshToken,
                    role = payload["role"] ?: "patient",
                    userId = payload["userId"] ?: ""
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception(translateError(response.error)))
            }
        } catch (e: Exception) {
            Result.failure(Exception(classifyNetworkError(e)))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        role: String,
        phone: String? = null,
        professionalRegistration: String? = null
    ): Result<Unit> {
        return try {
            val response = apiClient.httpClient.post("/auth/register") {
                setBody(RegisterRequest(email, password, fullName, role, phone, professionalRegistration))
            }.body<ApiWrapper<Map<String, String>>>()

            if (response.success) Result.success(Unit)
            else Result.failure(Exception(translateError(response.error)))
        } catch (e: Exception) {
            Result.failure(Exception(classifyNetworkError(e)))
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = apiClient.httpClient.post("/auth/forgot-password") {
                setBody(mapOf("email" to email))
            }.body<ApiWrapper<String>>()

            if (response.success) Result.success(Unit)
            else Result.failure(Exception(translateError(response.error)))
        } catch (e: Exception) {
            Result.failure(Exception("Sem conexão com o servidor. Verifique sua internet."))
        }
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            val response = apiClient.httpClient.post("/auth/reset-password") {
                setBody(mapOf("email" to email, "code" to code, "newPassword" to newPassword))
            }.body<ApiWrapper<String>>()

            if (response.success) Result.success(Unit)
            else Result.failure(Exception(translateError(response.error)))
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("Invalid reset code") == true -> "Código inválido"
                e.message?.contains("Reset code expired") == true -> "Código expirado. Solicite um novo."
                else -> translateError(e.message)
            }
            Result.failure(Exception(message))
        }
    }

    fun logout() {
        TokenStorage.clear()
    }

    private fun classifyNetworkError(e: Exception): String {
        val msg = e.message ?: return "Erro desconhecido: ${e::class.simpleName}"
        return when {
            msg.contains("Connection refused", ignoreCase = true) ->
                "Servidor não encontrado. Verifique se o backend está rodando na porta 8080."
            msg.contains("Unable to resolve host", ignoreCase = true) ->
                "Sem conexão com a internet."
            msg.contains("timeout", ignoreCase = true) ->
                "Tempo de conexão esgotado."
            msg.contains("Connect timed out", ignoreCase = true) ->
                "Servidor não respondeu a tempo."
            else -> "Erro: $msg"
        }
    }

    private fun translateError(error: String?): String = when {
        error == null -> "Erro desconhecido"
        error.contains("Invalid credentials", ignoreCase = true) -> "E-mail ou senha incorretos"
        error.contains("Account is deactivated", ignoreCase = true) -> "Conta desativada. Entre em contato com o suporte."
        error.contains("Email already registered", ignoreCase = true) -> "Este e-mail já está cadastrado"
        error.contains("Invalid email", ignoreCase = true) -> "Formato de e-mail inválido"
        error.contains("Password must be", ignoreCase = true) -> "A senha deve ter pelo menos 8 caracteres"
        error.contains("Full name is required", ignoreCase = true) -> "Nome completo é obrigatório"
        error.contains("Invalid role", ignoreCase = true) -> "Tipo de conta inválido"
        error.contains("Invalid reset code", ignoreCase = true) -> "Código de recuperação inválido"
        error.contains("Reset code expired", ignoreCase = true) -> "Código expirado. Solicite um novo."
        error.contains("User not found", ignoreCase = true) -> "Usuário não encontrado"
        else -> error
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
