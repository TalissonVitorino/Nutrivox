package com.kotlincrossplatform.nutrivox

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kotlincrossplatform.nutrivox.data.remote.ApiClient
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage
import com.kotlincrossplatform.nutrivox.navigation.*
import com.kotlincrossplatform.nutrivox.theme.NutrivoxTheme

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
                    // Placeholder — will be replaced by LoginScreen
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Login Screen — TODO")
                    }
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
