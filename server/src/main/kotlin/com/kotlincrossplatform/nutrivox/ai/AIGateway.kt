package com.kotlincrossplatform.nutrivox.ai

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface AIProvider {
    suspend fun complete(systemPrompt: String, userMessage: String): String
}

class AnthropicProvider(config: ApplicationConfig) : AIProvider {
    private val apiKey = config.property("ai.apiKey").getString()
    private val model = config.property("ai.model").getString()

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
        }
    }

    override suspend fun complete(systemPrompt: String, userMessage: String): String {
        if (apiKey.isBlank()) return "[AI unavailable - no API key configured]"

        val response = client.post("https://api.anthropic.com/v1/messages") {
            header("x-api-key", apiKey)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
            setBody(AnthropicRequest(
                model = model,
                max_tokens = 1024,
                system = systemPrompt,
                messages = listOf(AnthropicMessage("user", userMessage))
            ))
        }

        val body = response.bodyAsText()
        val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<AnthropicResponse>(body)
        return parsed.content.firstOrNull()?.text ?: "No response generated"
    }
}

@Serializable
data class AnthropicRequest(
    val model: String,
    val max_tokens: Int,
    val system: String,
    val messages: List<AnthropicMessage>
)

@Serializable
data class AnthropicMessage(val role: String, val content: String)

@Serializable
data class AnthropicResponse(val content: List<AnthropicContentBlock>)

@Serializable
data class AnthropicContentBlock(val type: String, val text: String = "")
