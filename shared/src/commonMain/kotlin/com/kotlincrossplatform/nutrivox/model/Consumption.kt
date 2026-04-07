package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionRecord(
    val id: String, val mealId: String?, val date: String,
    val time: String?, val mode: String, val notes: String?,
    val items: List<ConsumptionItem>
)

@Serializable
data class ConsumptionItem(
    val id: String, val foodName: String, val quantityGrams: Double?,
    val householdMeasure: String?, val wasConsumed: Boolean,
    val isSubstitution: Boolean, val substitutionOrigin: String?,
    val originalFoodName: String?, val isOffPlan: Boolean,
    val nutrients: NutrientValues
)

@Serializable
data class DailyTotals(
    val date: String, val consumed: NutrientValues,
    val mealsRegistered: Int
)
