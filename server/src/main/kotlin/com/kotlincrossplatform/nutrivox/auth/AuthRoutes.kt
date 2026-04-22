package com.kotlincrossplatform.nutrivox.auth

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.plugins.userId
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class RegisterBody(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String,
    val phone: String? = null,
    val professionalRegistration: String? = null
)

@Serializable
data class LoginBody(
    val email: String,
    val password: String
)

@Serializable
data class RefreshBody(
    val refreshToken: String
)

@Serializable
data class ForgotPasswordBody(
    val email: String
)

@Serializable
data class ResetPasswordBody(
    val email: String,
    val code: String,
    val newPassword: String
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class RegisterResponse(
    val userId: String
)

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val body = call.receive<RegisterBody>()
            val userId = authService.register(
                RegisterRequest(
                    email = body.email,
                    password = body.password,
                    fullName = body.fullName,
                    role = body.role,
                    phone = body.phone,
                    professionalRegistration = body.professionalRegistration
                )
            )
            call.respond(ApiResponse.ok(RegisterResponse(userId = userId.toString())))
        }

        post("/login") {
            val body = call.receive<LoginBody>()
            val tokenPair = authService.login(
                LoginRequest(
                    email = body.email,
                    password = body.password
                )
            )
            call.respond(
                ApiResponse.ok(
                    TokenResponse(
                        accessToken = tokenPair.accessToken,
                        refreshToken = tokenPair.refreshToken
                    )
                )
            )
        }

        post("/forgot-password") {
            val body = call.receive<ForgotPasswordBody>()
            val code = authService.requestPasswordReset(body.email)
            if (code != null) {
                call.application.environment.log.info("Password reset code for ${body.email}: $code")
            }
            // Always return success to avoid email enumeration
            call.respond(ApiResponse.ok("If the email exists, a reset code has been sent"))
        }

        post("/reset-password") {
            val body = call.receive<ResetPasswordBody>()
            authService.resetPassword(body.email, body.code, body.newPassword)
            call.respond(ApiResponse.ok("Password reset successfully"))
        }

        post("/refresh") {
            val body = call.receive<RefreshBody>()
            val tokenPair = authService.refresh(body.refreshToken)
            call.respond(
                ApiResponse.ok(
                    TokenResponse(
                        accessToken = tokenPair.accessToken,
                        refreshToken = tokenPair.refreshToken
                    )
                )
            )
        }

        authenticate("auth-jwt") {
            post("/logout") {
                val principal = call.principal<JWTPrincipal>()!!
                authService.logout(principal.userId())
                call.respond(ApiResponse.ok("Logged out successfully"))
            }
        }
    }
}
