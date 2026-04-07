package com.kotlincrossplatform.nutrivox.clinical

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.common.Exceptions.ForbiddenException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ValidationException
import com.kotlincrossplatform.nutrivox.common.paginationParams
import com.kotlincrossplatform.nutrivox.plugins.role
import com.kotlincrossplatform.nutrivox.plugins.userId
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.assessmentRoutes(assessmentService: AssessmentService) {
    route("/patients/{patientId}/assessments") {
        // Create assessment
        post {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can create assessments")

            val patientId = UUID.fromString(call.parameters["patientId"])
            val request = call.receive<CreateAssessmentRequest>()
            val assessmentId = assessmentService.createAssessment(patientId, principal.userId(), request)
            call.respond(ApiResponse.ok(mapOf("id" to assessmentId.toString())))
        }

        // List assessments for patient
        get {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can view assessments")

            val patientId = UUID.fromString(call.parameters["patientId"])
            val pagination = call.paginationParams()
            val result = assessmentService.listAssessments(patientId, pagination)
            call.respond(ApiResponse.ok(result))
        }
    }

    route("/assessments") {
        // Get assessment detail
        get("/{id}") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can view assessments")

            val assessmentId = UUID.fromString(call.parameters["id"])
            val detail = assessmentService.getAssessment(assessmentId)
            call.respond(ApiResponse.ok(detail))
        }

        // Compare two assessments
        get("/compare") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can compare assessments")

            val id1 = call.request.queryParameters["id1"]
                ?: throw ValidationException("id1 query parameter is required")
            val id2 = call.request.queryParameters["id2"]
                ?: throw ValidationException("id2 query parameter is required")

            val comparison = assessmentService.compareAssessments(
                UUID.fromString(id1),
                UUID.fromString(id2)
            )
            call.respond(ApiResponse.ok(comparison))
        }
    }
}
