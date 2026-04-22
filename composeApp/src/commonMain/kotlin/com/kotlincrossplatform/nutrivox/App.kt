package com.kotlincrossplatform.nutrivox

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kotlincrossplatform.nutrivox.data.remote.ApiClient
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage
import com.kotlincrossplatform.nutrivox.data.repository.AuthRepository
import com.kotlincrossplatform.nutrivox.data.repository.PlanRepository
import com.kotlincrossplatform.nutrivox.navigation.*
import com.kotlincrossplatform.nutrivox.theme.NutrivoxTheme
import com.kotlincrossplatform.nutrivox.ui.auth.AuthViewModel
import com.kotlincrossplatform.nutrivox.ui.auth.LoginScreen
import com.kotlincrossplatform.nutrivox.ui.auth.OnboardingScreen
import com.kotlincrossplatform.nutrivox.ui.auth.ForgotPasswordScreen
import com.kotlincrossplatform.nutrivox.ui.auth.RegisterScreen
import com.kotlincrossplatform.nutrivox.ui.components.BottomNavBar
import com.kotlincrossplatform.nutrivox.ui.components.BottomNavItem
import com.kotlincrossplatform.nutrivox.ui.components.NutrivoxTopBar
import com.kotlincrossplatform.nutrivox.ui.nutritionist.dashboard.DashboardScreen
import com.kotlincrossplatform.nutrivox.ui.nutritionist.patient_detail.PatientDetailScreen
import com.kotlincrossplatform.nutrivox.ui.nutritionist.patients.PatientListScreen
import com.kotlincrossplatform.nutrivox.ui.nutritionist.plan_editor.PlanEditorScreen
import com.kotlincrossplatform.nutrivox.ui.nutritionist.plan_editor.PlanPreviewScreen
import com.kotlincrossplatform.nutrivox.ui.nutritionist.settings.SettingsScreen
import com.kotlincrossplatform.nutrivox.ui.patient.chat.PatientChatScreen
import com.kotlincrossplatform.nutrivox.ui.patient.consumption.ConsumptionScreen
import com.kotlincrossplatform.nutrivox.ui.patient.home.PatientHomeScreen
import com.kotlincrossplatform.nutrivox.ui.patient.home.PatientHomeViewModel
import com.kotlincrossplatform.nutrivox.ui.patient.plan.MealDetailScreen
import com.kotlincrossplatform.nutrivox.ui.patient.profile.PatientProfileScreen
import com.kotlincrossplatform.nutrivox.ui.patient.progress.ProgressScreen

@Composable
fun App() {
    val navigationState = rememberNavigationState()
    val apiClient = remember {
        ApiClient(tokenProvider = { TokenStorage.accessToken })
    }
    val planRepository = remember { PlanRepository(apiClient) }

    fun logout() {
        TokenStorage.clear()
        navigationState.navigateAndClear(Screen.Login)
    }

    val screen = navigationState.currentScreen

    // Determine if we show bottom nav and which type
    val showNutriBottomNav = screen is Screen.Dashboard || screen is Screen.PatientList
            || screen is Screen.Settings || screen is Screen.PatientDetail
            || screen is Screen.PlanEditor || screen is Screen.PlanPreview
            || screen is Screen.NutriChat || screen is Screen.Assessment

    val showPatientBottomNav = screen is Screen.PatientHome || screen is Screen.Progress
            || screen is Screen.PatientChat || screen is Screen.PatientProfile
            || screen is Screen.MealDetail || screen is Screen.Consumption

    // Determine if we show top bar with back
    val topBarConfig = getTopBarConfig(screen)

    NutrivoxTheme {
        Scaffold(
            topBar = {
                if (topBarConfig != null) {
                    NutrivoxTopBar(
                        title = topBarConfig.title,
                        onBack = if (topBarConfig.showBack) {
                            { navigationState.goBack() }
                        } else null
                    )
                }
            },
            bottomBar = {
                when {
                    showNutriBottomNav -> NutriBottomNav(screen, navigationState)
                    showPatientBottomNav -> PatientBottomNav(screen, navigationState)
                }
            }
        ) { paddingValues ->
            Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (screen) {
                    // ── Auth ──
                    is Screen.Login -> {
                        val authViewModel = remember { AuthViewModel(AuthRepository(apiClient)) }
                        LoginScreen(
                            viewModel = authViewModel,
                            onLoginSuccess = { isNutritionist ->
                                if (isNutritionist) navigationState.navigateAndClear(Screen.Dashboard)
                                else navigationState.navigateAndClear(Screen.PatientHome)
                            },
                            onForgotPassword = {
                                navigationState.navigateTo(Screen.ForgotPassword)
                            },
                            onRegister = {
                                navigationState.navigateTo(Screen.Register)
                            }
                        )
                    }
                    is Screen.ForgotPassword -> {
                        val authRepository = remember { AuthRepository(apiClient) }
                        ForgotPasswordScreen(
                            authRepository = authRepository,
                            onBackToLogin = {
                                navigationState.navigateAndClear(Screen.Login)
                            }
                        )
                    }
                    is Screen.Register -> {
                        val authRepository = remember { AuthRepository(apiClient) }
                        RegisterScreen(
                            authRepository = authRepository,
                            onRegisterSuccess = { isNutritionist ->
                                if (isNutritionist) navigationState.navigateAndClear(Screen.Dashboard)
                                else navigationState.navigateAndClear(Screen.PatientHome)
                            },
                            onBackToLogin = {
                                navigationState.navigateAndClear(Screen.Login)
                            }
                        )
                    }
                    is Screen.Onboarding -> {
                        val authViewModel = remember { AuthViewModel(AuthRepository(apiClient)) }
                        OnboardingScreen(
                            inviteCode = screen.inviteCode,
                            viewModel = authViewModel,
                            onSuccess = { isNutritionist ->
                                if (isNutritionist) navigationState.navigateAndClear(Screen.Dashboard)
                                else navigationState.navigateAndClear(Screen.PatientHome)
                            }
                        )
                    }

                    // ── Patient ──
                    is Screen.PatientHome -> {
                        val viewModel = remember { PatientHomeViewModel(planRepository) }
                        PatientHomeScreen(
                            viewModel = viewModel,
                            onMealClick = { mealId ->
                                val meal = viewModel.plan?.variations
                                    ?.flatMap { it.meals }
                                    ?.find { it.id == mealId }
                                if (meal != null) navigationState.navigateTo(Screen.MealDetail(meal))
                            },
                            onRegisterConsumption = {
                                navigationState.navigateTo(Screen.Progress)
                            }
                        )
                    }
                    is Screen.MealDetail -> {
                        MealDetailScreen(
                            meal = screen.meal,
                            onRegisterConsumption = { _ ->
                                navigationState.navigateTo(Screen.Consumption(screen.meal))
                            },
                            onAISuggestion = { /* TODO */ },
                            onBack = { navigationState.goBack() }
                        )
                    }
                    is Screen.Consumption -> {
                        ConsumptionScreen(
                            meal = screen.meal,
                            onSave = { _, _ -> navigationState.goBack() },
                            onBack = { navigationState.goBack() }
                        )
                    }
                    is Screen.Progress -> {
                        ProgressScreen()
                    }
                    is Screen.PatientChat -> {
                        PatientChatScreen()
                    }
                    is Screen.PatientProfile -> {
                        PatientProfileScreen(onLogout = { logout() })
                    }

                    // ── Nutritionist ──
                    is Screen.Dashboard -> {
                        DashboardScreen(
                            onPatientClick = { patientId ->
                                navigationState.navigateTo(Screen.PatientDetail(patientId))
                            },
                            onViewAllPatients = {
                                navigationState.switchTab(Screen.PatientList)
                            }
                        )
                    }
                    is Screen.PatientList -> {
                        PatientListScreen(
                            onPatientClick = { patientId ->
                                navigationState.navigateTo(Screen.PatientDetail(patientId))
                            }
                        )
                    }
                    is Screen.PatientDetail -> {
                        PatientDetailScreen(
                            patientId = screen.patientId,
                            onEditPlan = {
                                navigationState.navigateTo(Screen.PlanEditor(screen.patientId))
                            },
                            onNewAssessment = {
                                navigationState.navigateTo(Screen.Assessment(screen.patientId))
                            },
                            onChat = {
                                navigationState.navigateTo(Screen.NutriChat(screen.patientId))
                            },
                            onBack = { navigationState.goBack() }
                        )
                    }
                    is Screen.PlanEditor -> {
                        PlanEditorScreen(
                            patientId = screen.patientId,
                            planId = screen.planId,
                            onPreview = { /* TODO */ },
                            onPublish = { navigationState.goBack() },
                            onBack = { navigationState.goBack() }
                        )
                    }
                    is Screen.PlanPreview -> {
                        PlanPreviewScreen(
                            planId = screen.planId,
                            onEdit = { navigationState.goBack() },
                            onPublish = {
                                navigationState.navigateAndClear(Screen.Dashboard)
                            },
                            onBack = { navigationState.goBack() }
                        )
                    }
                    is Screen.NutriChat -> {
                        PatientChatScreen()
                    }
                    is Screen.Settings -> {
                        SettingsScreen(onLogout = { logout() })
                    }
                    is Screen.Assessment -> {
                        PatientDetailScreen(
                            patientId = screen.patientId,
                            onBack = { navigationState.goBack() }
                        )
                    }
                }
            }
        }
    }
}

// ── Top bar configuration ──

private data class TopBarConfig(val title: String, val showBack: Boolean)

private fun getTopBarConfig(screen: Screen): TopBarConfig? = when (screen) {
    is Screen.Login, is Screen.Register, is Screen.ForgotPassword, is Screen.Onboarding -> null
    is Screen.Dashboard -> TopBarConfig("Painel Principal", showBack = false)
    is Screen.PatientList -> TopBarConfig("Pacientes", showBack = false)
    is Screen.Settings -> TopBarConfig("Configurações", showBack = false)
    is Screen.PatientHome -> TopBarConfig("Início", showBack = false)
    is Screen.Progress -> TopBarConfig("Evolução", showBack = false)
    is Screen.PatientChat -> TopBarConfig("Chat", showBack = false)
    is Screen.PatientProfile -> TopBarConfig("Perfil", showBack = false)
    is Screen.PatientDetail -> TopBarConfig("Paciente", showBack = true)
    is Screen.PlanEditor -> TopBarConfig("Editor de Plano", showBack = true)
    is Screen.PlanPreview -> TopBarConfig("Pré-visualização", showBack = true)
    is Screen.MealDetail -> TopBarConfig("Detalhes da Refeição", showBack = true)
    is Screen.Consumption -> TopBarConfig("Registrar Consumo", showBack = true)
    is Screen.NutriChat -> TopBarConfig("Chat com Paciente", showBack = true)
    is Screen.Assessment -> TopBarConfig("Avaliação", showBack = true)
}

// ── Bottom navigation bars ──

@Composable
private fun NutriBottomNav(currentScreen: Screen, navState: NavigationState) {
    val isOnDashboard = currentScreen is Screen.Dashboard
    val isOnPatients = currentScreen is Screen.PatientList
            || currentScreen is Screen.PatientDetail
            || currentScreen is Screen.PlanEditor
            || currentScreen is Screen.PlanPreview
            || currentScreen is Screen.NutriChat
            || currentScreen is Screen.Assessment
    val isOnSettings = currentScreen is Screen.Settings

    BottomNavBar(
        items = listOf(
            BottomNavItem("Painel", "📊", isOnDashboard) {
                navState.switchTab(Screen.Dashboard)
            },
            BottomNavItem("Pacientes", "👥", isOnPatients) {
                navState.switchTab(Screen.PatientList)
            },
            BottomNavItem("Config.", "⚙", isOnSettings) {
                navState.switchTab(Screen.Settings)
            }
        )
    )
}

@Composable
private fun PatientBottomNav(currentScreen: Screen, navState: NavigationState) {
    val isOnHome = currentScreen is Screen.PatientHome
            || currentScreen is Screen.MealDetail
            || currentScreen is Screen.Consumption
    val isOnProgress = currentScreen is Screen.Progress
    val isOnChat = currentScreen is Screen.PatientChat
    val isOnProfile = currentScreen is Screen.PatientProfile

    BottomNavBar(
        items = listOf(
            BottomNavItem("Início", "🏠", isOnHome) {
                navState.switchTab(Screen.PatientHome)
            },
            BottomNavItem("Evolução", "📈", isOnProgress) {
                navState.switchTab(Screen.Progress)
            },
            BottomNavItem("Chat", "💬", isOnChat) {
                navState.switchTab(Screen.PatientChat)
            },
            BottomNavItem("Perfil", "👤", isOnProfile) {
                navState.switchTab(Screen.PatientProfile)
            }
        )
    )
}
