package com.kotlincrossplatform.nutrivox.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient(
    private val baseUrl: String = "http://10.0.2.2:8080", // Android emulator localhost
    private val tokenProvider: () -> String? = { null }
) {
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
    }
}
