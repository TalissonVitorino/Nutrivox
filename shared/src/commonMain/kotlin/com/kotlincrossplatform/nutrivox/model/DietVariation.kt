package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class DietVariationDetail(
    val id: String, val name: String, val isDefault: Boolean,
    val isPatientAccessible: Boolean, val sortOrder: Int,
    val meals: List<MealDetail>
)
