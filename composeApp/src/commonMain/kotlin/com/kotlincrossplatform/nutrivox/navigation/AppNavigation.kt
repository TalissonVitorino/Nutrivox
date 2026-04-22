package com.kotlincrossplatform.nutrivox.navigation

import androidx.compose.runtime.*
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage

class NavigationState {
    var currentScreen by mutableStateOf<Screen>(resolveInitialScreen())
        private set

    private companion object {
        fun resolveInitialScreen(): Screen = when {
            !TokenStorage.isLoggedIn -> Screen.Login
            TokenStorage.isNutritionist -> Screen.Dashboard
            else -> Screen.PatientHome
        }
    }

    private val backStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        // Prevent duplicate pushes
        if (currentScreen == screen) return
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

    /** Navigate to a root tab (clears stack to the tab's root) */
    fun switchTab(screen: Screen) {
        // If already on this screen, do nothing
        if (currentScreen == screen) return
        // Clear any detail screens from the stack and go to root tab
        backStack.clear()
        currentScreen = screen
    }

    val canGoBack: Boolean get() = backStack.isNotEmpty()
}

@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}
