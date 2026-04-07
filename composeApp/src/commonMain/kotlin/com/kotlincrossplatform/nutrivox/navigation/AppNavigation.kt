package com.kotlincrossplatform.nutrivox.navigation

import androidx.compose.runtime.*

sealed class Screen {
    // Auth
    data object Login : Screen()
    data class Onboarding(val inviteCode: String) : Screen()

    // Patient
    data object PatientHome : Screen()
    data class PlanDetail(val variationId: String? = null) : Screen()
    data class Consumption(val mealId: String) : Screen()
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

class NavigationState {
    var currentScreen by mutableStateOf<Screen>(Screen.Login)
        private set

    private val backStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        backStack.add(currentScreen)
        currentScreen = screen
    }

    fun goBack(): Boolean {
        if (backStack.isEmpty()) return false
        currentScreen = backStack.removeLast()
        return true
    }

    fun navigateAndClear(screen: Screen) {
        backStack.clear()
        currentScreen = screen
    }
}

@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}
