package com.kotlincrossplatform.nutrivox.navigation

import com.kotlincrossplatform.nutrivox.data.repository.MealResponse

sealed class Screen {
    // Auth
    data object Login : Screen()
    data object Register : Screen()
    data object ForgotPassword : Screen()
    data class Onboarding(val inviteCode: String) : Screen()

    // Patient
    data object PatientHome : Screen()
    data class MealDetail(val meal: MealResponse) : Screen()
    data class Consumption(val meal: MealResponse) : Screen()
    data object Progress : Screen()
    data object PatientChat : Screen()
    data object PatientProfile : Screen()

    // Nutritionist
    data object Dashboard : Screen()
    data object PatientList : Screen()
    data class PatientDetail(val patientId: String) : Screen()
    data class PlanEditor(val patientId: String, val planId: String? = null) : Screen()
    data class PlanPreview(val planId: String) : Screen()
    data class NutriChat(val patientId: String) : Screen()
    data object Settings : Screen()
    data class Assessment(val patientId: String) : Screen()
}
