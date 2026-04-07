package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class PlanSummary(
    val id: String, val name: String, val status: String,
    val objective: String?, val startDate: String?,
    val goalCalories: Double?
)

@Serializable
data class PlanDetail(
    val id: String, val name: String, val status: String,
    val objective: String?, val startDate: String?, val endDate: String?,
    val generalNotes: String?,
    val goalCalories: Double?, val goalProteinG: Double?,
    val goalCarbsG: Double?, val goalFatG: Double?, val goalFiberG: Double?,
    val variations: List<DietVariationDetail>
)

@Serializable
data class NutritionalGoal(
    val calories: Double?, val proteinG: Double?,
    val carbsG: Double?, val fatG: Double?, val fiberG: Double?
)
