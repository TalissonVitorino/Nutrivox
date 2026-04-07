package com.kotlincrossplatform.nutrivox.ai

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.common.Exceptions.ValidationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.aiRoutes(aiService: AIService) {
    route("/ai") {
        post("/suggest-substitution") {
            val request = call.receive<SubstitutionSuggestionRequest>()
            val suggestions = aiService.suggestSubstitutions(request)
            call.respond(ApiResponse.ok(suggestions))
        }
        post("/chat") {
            val request = call.receive<ChatRequest>()
            val response = aiService.chat(request)
            call.respond(ApiResponse.ok(response))
        }
        get("/conversations") {
            val patientId = call.request.queryParameters["patientId"]
                ?: throw ValidationException("patientId required")
            val conversations = transaction {
                AIConversationTable.selectAll()
                    .where { AIConversationTable.patientId eq UUID.fromString(patientId) }
                    .orderBy(AIConversationTable.updatedAt, SortOrder.DESC)
                    .map { row ->
                        mapOf(
                            "id" to row[AIConversationTable.id].toString(),
                            "contextType" to row[AIConversationTable.contextType],
                            "createdAt" to row[AIConversationTable.createdAt].toString()
                        )
                    }
            }
            call.respond(ApiResponse.ok(conversations))
        }
    }
}
