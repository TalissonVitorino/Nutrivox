package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class AssessmentSummary(
    val id: String, val date: String, val weightKg: Double?,
    val bmi: Double?, val isDraft: Boolean
)

@Serializable
data class AssessmentDetail(
    val id: String, val date: String, val assessmentType: String,
    val weightKg: Double?, val heightCm: Double?, val bmi: Double?,
    val waistCm: Double?, val hipCm: Double?, val abdomenCm: Double?,
    val bodyFatPct: Double?, val muscleMassKg: Double?,
    val bodyWaterPct: Double?, val clinicalNotes: String?, val isDraft: Boolean
)
