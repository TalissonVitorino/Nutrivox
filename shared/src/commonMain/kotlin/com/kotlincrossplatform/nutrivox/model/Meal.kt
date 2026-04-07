package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class MealDetail(
    val id: String, val name: String, val suggestedTime: String?,
    val sortOrder: Int, val notes: String?,
    val items: List<MealItemDetail>,
    val totals: NutrientValues
)

@Serializable
data class MealSummary(
    val id: String, val name: String, val suggestedTime: String?,
    val totalCalories: Double, val totalProtein: Double,
    val totalCarbs: Double, val totalFat: Double,
    val consumptionStatus: String? = null
)
