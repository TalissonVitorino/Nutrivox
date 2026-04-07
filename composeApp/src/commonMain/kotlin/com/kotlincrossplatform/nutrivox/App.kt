package com.kotlincrossplatform.nutrivox

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kotlincrossplatform.nutrivox.data.remote.ApiClient
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage
import com.kotlincrossplatform.nutrivox.data.repository.AuthRepository
import com.kotlincrossplatform.nutrivox.navigation.*
import com.kotlincrossplatform.nutrivox.theme.NutrivoxTheme
import com.kotlincrossplatform.nutrivox.ui.auth.AuthViewModel
import com.kotlincrossplatform.nutrivox.ui.auth.LoginScreen
import com.kotlincrossplatform.nutrivox.ui.auth.OnboardingScreen

@Composable
fun App() {
    val navigationState = rememberNavigationState()
    val apiClient = remember {
        ApiClient(tokenProvider = { TokenStorage.accessToken })
    }

    NutrivoxTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (val screen = navigationState.currentScreen) {
                is Screen.Login -> {
                    val authViewModel = remember {
                        AuthViewModel(AuthRepository(apiClient))
                    }
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = { isNutritionist ->
                            if (isNutritionist) navigationState.navigateAndClear(Screen.Dashboard)
                            else navigationState.navigateAndClear(Screen.PatientHome)
                        }
                    )
                }
                is Screen.Onboarding -> {
                    val authViewModel = remember {
                        AuthViewModel(AuthRepository(apiClient))
                    }
                    OnboardingScreen(
                        inviteCode = screen.inviteCode,
                        viewModel = authViewModel,
                        onSuccess = { navigationState.navigateAndClear(Screen.Login) }
                    )
                }
                is Screen.PatientHome -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Patient Home — TODO")
                    }
                }
                is Screen.Dashboard -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nutritionist Dashboard — TODO")
                    }
                }
                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Screen: ${screen::class.simpleName} — TODO")
                    }
                }
            }
        }
    }
}
