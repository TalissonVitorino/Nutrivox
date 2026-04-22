package com.kotlincrossplatform.nutrivox.ai

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface AIProvider {
    suspend fun complete(systemPrompt: String, userMessage: String): String
}

class OpenAIProvider(config: ApplicationConfig) : AIProvider {
    private val apiKey = config.property("ai.apiKey").getString()
    private val model = config.property("ai.model").getString()

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
        }
    }

    override suspend fun complete(systemPrompt: String, userMessage: String): String {
        if (apiKey.isBlank()) return "[AI unavailable - no API key configured]"

        val response = client.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(OpenAIRequest(
                model = model,
                maxTokens = 1024,
                messages = listOf(
                    OpenAIMessage(role = "system", content = systemPrompt),
                    OpenAIMessage(role = "user", content = userMessage)
                )
            ))
        }

        val body = response.bodyAsText()
        val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<OpenAIResponse>(body)
        return parsed.choices.firstOrNull()?.message?.content ?: "No response generated"
    }
}

@Serializable
data class OpenAIRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<OpenAIMessage>
)

@Serializable
data class OpenAIMessage(val role: String, val content: String)

@Serializable
data class OpenAIResponse(val choices: List<OpenAIChoice>)

@Serializable
data class OpenAIChoice(val message: OpenAIMessage)
