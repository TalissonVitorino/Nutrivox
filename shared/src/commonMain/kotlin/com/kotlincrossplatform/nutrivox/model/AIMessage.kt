package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class AIConversation(
    val id: String, val contextType: String, val createdAt: String
)

@Serializable
data class AIChatMessage(
    val role: String, val content: String, val createdAt: String
)

@Serializable
data class AISubstitutionSuggestion(
    val foodName: String, val quantityGrams: Double,
    val householdMeasure: String?, val nutrients: NutrientValues,
    val reason: String
)
