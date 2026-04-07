package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodSummary(
    val id: String, val name: String, val category: String?,
    val caloriesPer100g: Double?, val proteinPer100g: Double?,
    val carbsPer100g: Double?, val fatPer100g: Double?
)

@Serializable
data class FoodDetail(
    val id: String, val name: String, val category: String?,
    val caloriesPer100g: Double?, val proteinPer100g: Double?,
    val carbsPer100g: Double?, val fatPer100g: Double?,
    val fiberPer100g: Double?, val sodiumPer100g: Double?,
    val source: String?,
    val householdMeasures: List<HouseholdMeasure>
)

@Serializable
data class HouseholdMeasure(
    val id: String, val name: String, val grams: Double
)
