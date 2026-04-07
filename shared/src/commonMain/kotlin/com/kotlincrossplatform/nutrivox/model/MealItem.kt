package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class MealItemDetail(
    val id: String, val foodId: String?, val foodName: String,
    val quantityGrams: Double?, val householdMeasure: String?,
    val isAdLibitum: Boolean, val notes: String?,
    val nutrients: NutrientValues,
    val substitutions: List<SubstitutionDetail>
)

@Serializable
data class SubstitutionDetail(
    val id: String, val foodId: String?, val foodName: String,
    val quantityGrams: Double?, val householdMeasure: String?,
    val nutrients: NutrientValues, val notes: String?
)
