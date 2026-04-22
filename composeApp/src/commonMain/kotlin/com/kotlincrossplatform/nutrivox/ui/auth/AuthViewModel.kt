package com.kotlincrossplatform.nutrivox.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage
import com.kotlincrossplatform.nutrivox.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var isPasswordVisible by mutableStateOf(false)
    var isNutritionistMode by mutableStateOf(false)

    // Onboarding
    var inviteCode by mutableStateOf("")
    var fullName by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var acceptedTerms by mutableStateOf(false)
    var aiConsent by mutableStateOf(false)

    private val scope = CoroutineScope(Dispatchers.Default)

    fun login(onSuccess: (isNutritionist: Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            error = "Preencha todos os campos"
            return
        }
        isLoading = true
        error = null
        scope.launch {
            val result = authRepository.login(email, password)
            isLoading = false
            result.fold(
                onSuccess = { onSuccess(TokenStorage.isNutritionist) },
                onFailure = { error = it.message ?: "Erro ao fazer login" }
            )
        }
    }

    fun acceptInvite(onSuccess: (isNutritionist: Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            error = "Preencha todos os campos"
            return
        }
        if (password != confirmPassword) {
            error = "As senhas não coincidem"
            return
        }
        if (password.length < 8) {
            error = "A senha deve ter pelo menos 8 caracteres"
            return
        }
        if (!acceptedTerms) {
            error = "Aceite os termos de uso"
            return
        }
        isLoading = true
        error = null
        scope.launch {
            val result = authRepository.register(email, password, fullName, "patient")
            result.fold(
                onSuccess = {
                    // Auto-login after registration
                    val loginResult = authRepository.login(email, password)
                    isLoading = false
                    loginResult.fold(
                        onSuccess = { onSuccess(TokenStorage.isNutritionist) },
                        onFailure = { onSuccess(false) }
                    )
                },
                onFailure = {
                    isLoading = false
                    error = it.message ?: "Erro ao criar conta"
                }
            )
        }
    }

    fun clearError() { error = null }
}
