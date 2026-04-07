package com.kotlincrossplatform.nutrivox.model

import kotlinx.serialization.Serializable

enum class UserRole { NUTRITIONIST, PATIENT, ADMIN }

@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val phone: String? = null,
    val professionalRegistration: String? = null
)
