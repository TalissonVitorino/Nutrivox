package com.kotlincrossplatform.nutrivox.data.remote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings

object TokenStorage {
    private val settings: Settings = Settings()

    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_ID = "user_id"

    var accessToken: String? by mutableStateOf(settings.getStringOrNull(KEY_ACCESS_TOKEN))
        private set
    var refreshToken: String? by mutableStateOf(settings.getStringOrNull(KEY_REFRESH_TOKEN))
        private set
    var userRole: String? by mutableStateOf(settings.getStringOrNull(KEY_USER_ROLE))
        private set
    var userId: String? by mutableStateOf(settings.getStringOrNull(KEY_USER_ID))
        private set

    fun saveTokens(access: String, refresh: String, role: String, userId: String) {
        accessToken = access
        refreshToken = refresh
        userRole = role
        this.userId = userId

        settings.putString(KEY_ACCESS_TOKEN, access)
        settings.putString(KEY_REFRESH_TOKEN, refresh)
        settings.putString(KEY_USER_ROLE, role)
        settings.putString(KEY_USER_ID, userId)
    }

    fun clear() {
        accessToken = null
        refreshToken = null
        userRole = null
        userId = null

        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_USER_ROLE)
        settings.remove(KEY_USER_ID)
    }

    val isLoggedIn: Boolean get() = accessToken != null
    val isNutritionist: Boolean get() = userRole == "nutritionist"
    val isPatient: Boolean get() = userRole == "patient"
}
