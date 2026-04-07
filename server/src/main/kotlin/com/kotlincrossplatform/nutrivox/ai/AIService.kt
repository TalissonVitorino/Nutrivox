package com.kotlincrossplatform.nutrivox.ai

import com.kotlincrossplatform.nutrivox.common.Exceptions.NotFoundException
import com.kotlincrossplatform.nutrivox.patients.PatientTable
import com.kotlincrossplatform.nutrivox.plans.MealPlanTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.util.*

@Serializable
data class SubstitutionSuggestionRequest(
    val mealItemId: String,
    val foodName: String,
    val currentCalories: Double?,
    val patientId: String
)

@Serializable
data class SubstitutionSuggestion(
    val foodName: String,
    val quantityGrams: Double,
    val householdMeasure: String?,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val reason: String
)

@Serializable
data class ChatRequest(val patientId: String, val message: String, val conversationId: String? = null)

@Serializable
data class ChatResponse(val conversationId: String, val response: String)

class AIService(private val provider: AIProvider) {

    suspend fun suggestSubstitutions(request: SubstitutionSuggestionRequest): List<SubstitutionSuggestion> {
        val patientContext = getPatientContext(UUID.fromString(request.patientId))

        val systemPrompt = """You are a nutritional assistant for the Nutrivox app.
You suggest food substitutions that match the patient's nutritional goals and restrictions.
NEVER prescribe. Always suggest alternatives with similar nutritional profiles.
Respond ONLY in valid JSON array format.
Patient context: $patientContext"""

        val userPrompt = """Suggest 3 substitutions for "${request.foodName}" (${request.currentCalories} kcal).
Return JSON array: [{"foodName":"...","quantityGrams":0,"householdMeasure":"...","calories":0,"proteinG":0,"carbsG":0,"fatG":0,"reason":"..."}]"""

        val response = provider.complete(systemPrompt, userPrompt)

        return try {
            kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                .decodeFromString<List<SubstitutionSuggestion>>(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun chat(request: ChatRequest): ChatResponse {
        val patientId = UUID.fromString(request.patientId)
        val patientContext = getPatientContext(patientId)

        val systemPrompt = """You are a nutritional assistant for the Nutrivox app.
You answer questions about nutrition, food, hydration, and meal planning.
You NEVER prescribe medication, diagnose conditions, or suggest supplements.
You NEVER associate foods/teas with specific clinical conditions (e.g., "tea for anxiety").
Always use careful, non-prescriptive language like "an option would be..." or "considering your plan..."
End responses with: "Consult your nutritionist for personalized adjustments."
Patient context: $patientContext"""

        val response = provider.complete(systemPrompt, request.message)

        val conversationId = transaction {
            val convId = if (request.conversationId != null) {
                UUID.fromString(request.conversationId)
            } else {
                AIConversationTable.insert {
                    it[AIConversationTable.patientId] = patientId
                    it[contextType] = "chat"
                    it[createdAt] = OffsetDateTime.now()
                    it[updatedAt] = OffsetDateTime.now()
                }[AIConversationTable.id]
            }

            AIMessageTable.insert {
                it[conversationId] = convId
                it[role] = "user"
                it[content] = request.message
                it[createdAt] = OffsetDateTime.now()
            }
            AIMessageTable.insert {
                it[conversationId] = convId
                it[role] = "assistant"
                it[content] = response
                it[createdAt] = OffsetDateTime.now()
            }

            convId
        }

        return ChatResponse(conversationId.toString(), response)
    }

    private fun getPatientContext(patientId: UUID): String = transaction {
        val patient = PatientTable.selectAll()
            .where { PatientTable.id eq patientId }
            .singleOrNull() ?: throw NotFoundException("Patient not found")

        val activePlan = MealPlanTable.selectAll()
            .where { (MealPlanTable.patientId eq patientId) and (MealPlanTable.status eq "active") }
            .singleOrNull()

        buildString {
            append("Goal: ${patient[PatientTable.primaryGoal] ?: "not specified"}. ")
            append("Restrictions: ${patient[PatientTable.dietaryRestrictions] ?: "none"}. ")
            if (activePlan != null) {
                append("Active plan: ${activePlan[MealPlanTable.name]}. ")
                append("Calorie goal: ${activePlan[MealPlanTable.goalCalories]?.toDouble() ?: "not set"} kcal. ")
            }
        }
    }
}
