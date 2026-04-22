package com.kotlincrossplatform.nutrivox

import com.kotlincrossplatform.nutrivox.ai.AIService
import com.kotlincrossplatform.nutrivox.ai.OpenAIProvider
import com.kotlincrossplatform.nutrivox.ai.aiRoutes
import com.kotlincrossplatform.nutrivox.auth.AuthService
import com.kotlincrossplatform.nutrivox.auth.JwtConfig
import com.kotlincrossplatform.nutrivox.auth.authRoutes
import com.kotlincrossplatform.nutrivox.clinical.AssessmentService
import com.kotlincrossplatform.nutrivox.clinical.ClinicalRecordService
import com.kotlincrossplatform.nutrivox.clinical.assessmentRoutes
import com.kotlincrossplatform.nutrivox.clinical.clinicalRecordRoutes
import com.kotlincrossplatform.nutrivox.consumption.ConsumptionService
import com.kotlincrossplatform.nutrivox.consumption.consumptionRoutes
import com.kotlincrossplatform.nutrivox.foods.FoodService
import com.kotlincrossplatform.nutrivox.foods.foodRoutes
import com.kotlincrossplatform.nutrivox.patients.InviteService
import com.kotlincrossplatform.nutrivox.patients.PatientService
import com.kotlincrossplatform.nutrivox.patients.patientRoutes
import com.kotlincrossplatform.nutrivox.plans.PlanService
import com.kotlincrossplatform.nutrivox.plans.planRoutes
import com.kotlincrossplatform.nutrivox.plugins.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    // Plugins
    configureDatabase()
    configureContentNegotiation()
    configureStatusPages()
    configureAuthentication()

    // Seed dev users (only in local environment)
    seedDevUsers()

    // Config
    val jwtConfig = JwtConfig(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        accessTokenExpiration = environment.config.property("jwt.accessTokenExpiration").getString().toLong(),
        refreshTokenExpiration = environment.config.property("jwt.refreshTokenExpiration").getString().toLong()
    )

    // Services
    val authService = AuthService(jwtConfig)
    val patientService = PatientService()
    val inviteService = InviteService()
    val clinicalRecordService = ClinicalRecordService()
    val assessmentService = AssessmentService()
    val foodService = FoodService()
    val planService = PlanService()
    val consumptionService = ConsumptionService()
    val aiProvider = OpenAIProvider(environment.config)
    val aiService = AIService(aiProvider)

    // Routes
    routing {
        // Health check
        get("/") { call.respondText("Nutrivox API v1.0.0") }
        get("/health") { call.respondText("OK") }

        // Auth (public routes + internally authenticated logout)
        authRoutes(authService)

        // Patient routes (public /invites/accept + internally authenticated /patients/*)
        patientRoutes(patientService, inviteService)

        // All other routes require authentication
        authenticate("auth-jwt") {
            clinicalRecordRoutes(clinicalRecordService)
            assessmentRoutes(assessmentService)
            foodRoutes(foodService)
            planRoutes(planService)
            consumptionRoutes(consumptionService)
            aiRoutes(aiService)
        }
    }
}
