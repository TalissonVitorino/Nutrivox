package com.kotlincrossplatform.nutrivox.plans

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.common.Exceptions.ForbiddenException
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

fun Route.planRoutes(planService: PlanService) {

    // Patient-scoped routes
    route("/patients/{patientId}/plans") {

        // POST /patients/{patientId}/plans — create plan (nutritionist only)
        post {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can create plans")

            val patientId = UUID.fromString(call.parameters["patientId"])
            val body = call.receive<CreatePlanRequest>()
            val planId = planService.createPlan(patientId, principal.userId(), body)
            call.respond(HttpStatusCode.Created, ApiResponse.ok(mapOf("id" to planId.toString())))
        }

        // GET /patients/{patientId}/plans — list plans (nutritionist only)
        get {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can list plans")

            val patientId = UUID.fromString(call.parameters["patientId"])
            val pagination = call.paginationParams()
            val result = planService.listPatientPlans(patientId, pagination)
            call.respond(ApiResponse.ok(result))
        }

        // GET /patients/{patientId}/plans/active — get active plan (patient or nutritionist)
        get("/active") {
            val principal = call.principal<JWTPrincipal>()!!
            val patientId = UUID.fromString(call.parameters["patientId"])
            val plan = planService.getActivePlan(patientId)
            if (plan != null) {
                call.respond(ApiResponse.ok(plan))
            } else {
                call.respond(ApiResponse.ok<PlanDetailDTO?>(null))
            }
        }
    }

    // Plan-scoped routes
    route("/plans/{id}") {

        // GET /plans/{id} — plan detail
        get {
            val planId = UUID.fromString(call.parameters["id"])
            val detail = planService.getPlanDetail(planId)
            call.respond(ApiResponse.ok(detail))
        }

        // PUT /plans/{id} — update plan (nutritionist, draft only)
        put {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can update plans")

            val planId = UUID.fromString(call.parameters["id"])
            val body = call.receive<CreatePlanRequest>()
            planService.updatePlan(planId, body)
            call.respond(ApiResponse.ok(mapOf("updated" to true)))
        }

        // POST /plans/{id}/activate — activate plan (nutritionist only)
        post("/activate") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can activate plans")

            val planId = UUID.fromString(call.parameters["id"])
            planService.activatePlan(planId, principal.userId())
            call.respond(ApiResponse.ok(mapOf("activated" to true)))
        }

        // POST /plans/{id}/deactivate — deactivate plan (nutritionist only)
        post("/deactivate") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can deactivate plans")

            val planId = UUID.fromString(call.parameters["id"])
            planService.deactivatePlan(planId)
            call.respond(ApiResponse.ok(mapOf("deactivated" to true)))
        }

        // POST /plans/{id}/duplicate — duplicate plan (nutritionist only)
        post("/duplicate") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.role() != "nutritionist") throw ForbiddenException("Only nutritionists can duplicate plans")

            val planId = UUID.fromString(call.parameters["id"])
            val newId = planService.duplicatePlan(planId, principal.userId())
            call.respond(HttpStatusCode.Created, ApiResponse.ok(mapOf("id" to newId.toString())))
        }
    }
}
