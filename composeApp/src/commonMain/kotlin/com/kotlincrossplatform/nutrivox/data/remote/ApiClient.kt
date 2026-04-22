package com.kotlincrossplatform.nutrivox.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class RefreshRequest(val refreshToken: String)

@Serializable
private data class RefreshApiResponse(
    val success: Boolean,
    val data: RefreshTokenData? = null,
    val error: String? = null
)

@Serializable
private data class RefreshTokenData(
    val accessToken: String,
    val refreshToken: String
)

class ApiClient(
    private val baseUrl: String = getApiBaseUrl(),
    private val tokenProvider: () -> String? = { null }
) {
    private val refreshMutex = Mutex()

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = false
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
            val token = tokenProvider()
            if (token != null) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    val refreshed = tryRefreshToken()
                    if (!refreshed) {
                        TokenStorage.clear()
                    }
                }
            }
        }
    }

    private suspend fun tryRefreshToken(): Boolean {
        val currentRefreshToken = TokenStorage.refreshToken ?: return false

        return refreshMutex.withLock {
            try {
                val refreshClient = HttpClient {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                        })
                    }
                }
                val response = refreshClient.post("$baseUrl/auth/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(RefreshRequest(currentRefreshToken))
                }
                refreshClient.close()

                if (response.status == HttpStatusCode.OK) {
                    val body = response.body<RefreshApiResponse>()
                    if (body.success && body.data != null) {
                        TokenStorage.saveTokens(
                            access = body.data.accessToken,
                            refresh = body.data.refreshToken,
                            role = TokenStorage.userRole ?: "patient",
                            userId = TokenStorage.userId ?: ""
                        )
                        true
                    } else false
                } else false
            } catch (e: Exception) {
                false
            }
        }
    }
}
