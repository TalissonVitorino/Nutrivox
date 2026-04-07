package com.kotlincrossplatform.nutrivox.patients

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.common.Exceptions.ForbiddenException
import com.kotlincrossplatform.nutrivox.common.paginationParams
import com.kotlincrossplatform.nutrivox.plugins.role
import com.kotlincrossplatform.nutrivox.plugins.userId
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateInviteBody(
    val patientName: String,
    val patientEmail: String? = null,
    val patientPhone: String? = null,
    val patientSex: String,
    val patientDateOfBirth: String,
    val patientGoal: String? = null,
    val patientRestrictions: String? = null,
    val patientNotes: String? = null
)

@Serializable
data class AcceptInviteBody(
    val inviteCode: String,
    val email: String,
    val password: String
)

fun Route.patientRoutes(patientService: PatientService, inviteService: InviteService) {
    route("/patients") {
        // Nutritionist creates invite (authenticated)
        post("/invite") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can invite")

            val body = call.receive<CreateInviteBody>()
            val code = inviteService.createInvite(
                nutritionistId = principal.userId(),
                request = CreateInviteRequest(
                    patientName = body.patientName,
                    patientEmail = body.patientEmail,
                    patientPhone = body.patientPhone,
                    patientSex = body.patientSex,
                    patientDateOfBirth = kotlinx.datetime.LocalDate.parse(body.patientDateOfBirth),
                    patientGoal = body.patientGoal,
                    patientRestrictions = body.patientRestrictions,
                    patientNotes = body.patientNotes
                )
            )
            call.respond(ApiResponse.ok(mapOf("inviteCode" to code)))
        }

        // List nutritionist's patients (authenticated)
        get {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can list patients")

            val pagination = call.paginationParams()
            val nameFilter = call.request.queryParameters["name"]
            val result = patientService.listPatients(principal.userId(), pagination, nameFilter)
            call.respond(ApiResponse.ok(result))
        }

        // Get patient detail (authenticated)
        get("/{id}") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can view patient details")

            val patientId = UUID.fromString(call.parameters["id"])
            val detail = patientService.getPatientDetail(principal.userId(), patientId)
            call.respond(ApiResponse.ok(detail))
        }
    }

    // Public endpoint: patient accepts invite (no auth required)
    route("/invites") {
        post("/accept") {
            val body = call.receive<AcceptInviteBody>()
            val userId = inviteService.acceptInvite(
                AcceptInviteRequest(body.inviteCode, body.email, body.password)
            )
            call.respond(ApiResponse.ok(mapOf("userId" to userId.toString())))
        }
    }
}
