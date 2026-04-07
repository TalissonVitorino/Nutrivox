package com.kotlincrossplatform.nutrivox.navigation

object Routes {
    // Auth
    const val LOGIN = "login"
    const val ONBOARDING = "onboarding/{inviteCode}"

    // Patient tabs
    const val PATIENT_HOME = "patient/home"
    const val PATIENT_PLAN_DETAIL = "patient/plan/{variationId}"
    const val PATIENT_CONSUMPTION = "patient/consumption/{mealId}"
    const val PATIENT_PROGRESS = "patient/progress"
    const val PATIENT_CHAT = "patient/chat"
    const val PATIENT_PROFILE = "patient/profile"

    // Nutritionist tabs
    const val NUTRI_DASHBOARD = "nutri/dashboard"
    const val NUTRI_PATIENTS = "nutri/patients"
    const val NUTRI_PATIENT_DETAIL = "nutri/patient/{patientId}"
    const val NUTRI_PLAN_EDITOR = "nutri/plan-editor/{patientId}/{planId}"
    const val NUTRI_PLAN_PREVIEW = "nutri/plan-preview/{planId}"
    const val NUTRI_CHAT = "nutri/chat/{patientId}"
    const val NUTRI_SETTINGS = "nutri/settings"
    const val NUTRI_ASSESSMENT = "nutri/assessment/{patientId}"
}
