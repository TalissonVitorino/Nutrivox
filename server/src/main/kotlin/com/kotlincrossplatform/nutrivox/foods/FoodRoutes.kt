package com.kotlincrossplatform.nutrivox.foods

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.common.paginationParams
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.foodRoutes(foodService: FoodService) {
    route("/foods") {
        get("/search") {
            val query = call.request.queryParameters["q"] ?: ""
            val category = call.request.queryParameters["category"]
            val pagination = call.paginationParams()
            val result = foodService.searchFoods(query, category, pagination)
            call.respond(ApiResponse.ok(result))
        }

        get("/{id}") {
            val foodId = UUID.fromString(call.parameters["id"])
            val detail = foodService.getFoodDetail(foodId)
            call.respond(ApiResponse.ok(detail))
        }

        get("/{id}/measures") {
            val foodId = UUID.fromString(call.parameters["id"])
            val measures = foodService.getFoodMeasures(foodId)
            call.respond(ApiResponse.ok(measures))
        }
    }
}
