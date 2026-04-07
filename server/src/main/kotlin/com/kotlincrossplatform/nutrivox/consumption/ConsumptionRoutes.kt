package com.kotlincrossplatform.nutrivox.consumption

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.common.Exceptions.ForbiddenException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ValidationException
import com.kotlincrossplatform.nutrivox.common.paginationParams
import com.kotlincrossplatform.nutrivox.plugins.role
import com.kotlincrossplatform.nutrivox.plugins.userId
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.consumptionRoutes(consumptionService: ConsumptionService) {

    route("/consumption") {

        // POST /consumption — register consumption (patient or nutritionist)
        post {
            val principal = call.principal<JWTPrincipal>()!!
            val role = principal.role()

            val body = call.receive<CreateConsumptionRequest>()

            val patientId = when (role) {
                "patient" -> consumptionService.resolvePatientId(principal.userId())
                "nutritionist" -> {
                    val pid = call.request.queryParameters["patientId"]
                        ?: throw ValidationException("patientId query parameter is required for nutritionists")
                    UUID.fromString(pid)
                }
                else -> throw ForbiddenException("Only patients and nutritionists can register consumption")
            }

            val recordId = consumptionService.registerConsumption(patientId, body)
            call.respond(HttpStatusCode.Created, ApiResponse.ok(mapOf("id" to recordId.toString())))
        }

        // GET /consumption/history?patientId=...&date=2026-04-07&page=1
        get("/history") {
            val principal = call.principal<JWTPrincipal>()!!
            val role = principal.role()

            val patientId = when (role) {
                "patient" -> consumptionService.resolvePatientId(principal.userId())
                "nutritionist" -> {
                    val pid = call.request.queryParameters["patientId"]
                        ?: throw ValidationException("patientId query parameter is required for nutritionists")
                    UUID.fromString(pid)
                }
                else -> throw ForbiddenException("Only patients and nutritionists can view consumption history")
            }

            val date = call.request.queryParameters["date"]
            val pagination = call.paginationParams()
            val result = consumptionService.getConsumptionHistory(patientId, date, pagination)
            call.respond(ApiResponse.ok(result))
        }

        // GET /consumption/daily-totals?patientId=...&date=2026-04-07
        get("/daily-totals") {
            val principal = call.principal<JWTPrincipal>()!!
            val role = principal.role()

            val patientId = when (role) {
                "patient" -> consumptionService.resolvePatientId(principal.userId())
                "nutritionist" -> {
                    val pid = call.request.queryParameters["patientId"]
                        ?: throw ValidationException("patientId query parameter is required for nutritionists")
                    UUID.fromString(pid)
                }
                else -> throw ForbiddenException("Only patients and nutritionists can view daily totals")
            }

            val date = call.request.queryParameters["date"]
                ?: throw ValidationException("date query parameter is required")

            val totals = consumptionService.getDailyTotals(patientId, date)
            call.respond(ApiResponse.ok(totals))
        }

        // DELETE /consumption/{id} — delete (patient only, same day)
        delete("/{id}") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "patient") {
                throw ForbiddenException("Only patients can delete consumption records")
            }

            val recordId = UUID.fromString(call.parameters["id"])
            val patientId = consumptionService.resolvePatientId(principal.userId())

            consumptionService.deleteConsumption(recordId, patientId)
            call.respond(ApiResponse.ok(mapOf("deleted" to true)))
        }
    }
}
