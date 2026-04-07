package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class ClinicalRecordDetail(
    val id: String, val patientId: String,
    val chiefComplaint: String?, val familyHistory: String?,
    val pathologies: String?, val intolerances: String?,
    val allergies: String?, val medications: String?,
    val supplementation: String?, val bowelHabits: String?,
    val sleepPattern: String?, val physicalActivity: String?,
    val waterIntake: String?, val foodPreferences: String?,
    val foodAversions: String?
)

@Serializable
data class EvolutionSummary(
    val id: String, val date: String,
    val generalNotes: String?, val adjustments: String?
)
