package com.kotlincrossplatform.nutrivox.clinical

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
import java.util.*

fun Route.clinicalRecordRoutes(clinicalRecordService: ClinicalRecordService) {
    route("/patients/{patientId}") {
        // Get or create clinical record
        get("/clinical-record") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can access clinical records")

            val patientId = UUID.fromString(call.parameters["patientId"])
            val record = clinicalRecordService.getOrCreateRecord(patientId)
            call.respond(ApiResponse.ok(record))
        }

        // Update clinical record
        put("/clinical-record") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can update clinical records")

            val patientId = UUID.fromString(call.parameters["patientId"])
            val request = call.receive<UpdateClinicalRecordRequest>()
            val record = clinicalRecordService.updateRecord(patientId, request)
            call.respond(ApiResponse.ok(record))
        }

        // Add evolution note
        post("/evolutions") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can add evolutions")

            val patientId = UUID.fromString(call.parameters["patientId"])
            val request = call.receive<CreateEvolutionRequest>()
            val evolutionId = clinicalRecordService.addEvolution(patientId, principal.userId(), request)
            call.respond(ApiResponse.ok(mapOf("id" to evolutionId.toString())))
        }

        // List evolutions
        get("/evolutions") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can view evolutions")

            val patientId = UUID.fromString(call.parameters["patientId"])
            val pagination = call.paginationParams()
            val result = clinicalRecordService.listEvolutions(patientId, pagination)
            call.respond(ApiResponse.ok(result))
        }
    }
}
