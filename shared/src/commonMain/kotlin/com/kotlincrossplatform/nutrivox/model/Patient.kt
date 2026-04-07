package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

@Serializable
data class PatientSummary(
    val id: String, val userId: String, val fullName: String,
    val sex: String, val dateOfBirth: String,
    val primaryGoal: String?, val isActive: Boolean
)

@Serializable
data class PatientDetail(
    val id: String, val userId: String, val fullName: String,
    val email: String, val phone: String?, val sex: String,
    val dateOfBirth: String, val primaryGoal: String?,
    val dietaryRestrictions: String?, val clinicalNotes: String?,
    val aiConsent: Boolean
)
