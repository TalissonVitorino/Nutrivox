package com.kotlincrossplatform.nutrivox.data.remote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object TokenStorage {
    var accessToken: String? by mutableStateOf(null)
        private set
    var refreshToken: String? by mutableStateOf(null)
        private set
    var userRole: String? by mutableStateOf(null)
        private set
    var userId: String? by mutableStateOf(null)
        private set

    fun saveTokens(access: String, refresh: String, role: String, userId: String) {
        accessToken = access
        refreshToken = refresh
        userRole = role
        this.userId = userId
    }

    fun clear() {
        accessToken = null
        refreshToken = null
        userRole = null
        userId = null
    }

    val isLoggedIn: Boolean get() = accessToken != null
    val isNutritionist: Boolean get() = userRole == "nutritionist"
    val isPatient: Boolean get() = userRole == "patient"
}
