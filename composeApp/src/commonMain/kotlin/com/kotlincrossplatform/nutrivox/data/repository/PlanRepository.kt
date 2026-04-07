package com.kotlincrossplatform.nutrivox.data.repository

import com.kotlincrossplatform.nutrivox.data.remote.ApiClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class PlanDetailResponse(
    val id: String,
    val name: String,
    val status: String,
    val objective: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val generalNotes: String? = null,
    val goalCalories: Double? = null,
    val goalProteinG: Double? = null,
    val goalCarbsG: Double? = null,
    val goalFatG: Double? = null,
    val goalFiberG: Double? = null,
    val variations: List<VariationResponse> = emptyList()
)

@Serializable
data class VariationResponse(
    val id: String,
    val name: String,
    val isDefault: Boolean = false,
    val isPatientAccessible: Boolean = true,
    val sortOrder: Int = 0,
    val meals: List<MealResponse> = emptyList()
)

@Serializable
data class MealResponse(
    val id: String,
    val name: String,
    val suggestedTime: String? = null,
    val sortOrder: Int = 0,
    val notes: String? = null,
    val items: List<MealItemResponse> = emptyList(),
    val totalCalories: Double = 0.0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0
)

@Serializable
data class MealItemResponse(
    val id: String,
    val foodId: String? = null,
    val foodName: String,
    val quantityGrams: Double? = null,
    val householdMeasure: String? = null,
    val isAdLibitum: Boolean = false,
    val notes: String? = null,
    val calories: Double? = null,
    val proteinG: Double? = null,
    val carbsG: Double? = null,
    val fatG: Double? = null,
    val fiberG: Double? = null,
    val substitutions: List<SubstitutionResponse> = emptyList()
)

@Serializable
data class SubstitutionResponse(
    val id: String,
    val foodId: String? = null,
    val foodName: String,
    val quantityGrams: Double? = null,
    val householdMeasure: String? = null,
    val calories: Double? = null,
    val proteinG: Double? = null,
    val carbsG: Double? = null,
    val fatG: Double? = null,
    val notes: String? = null
)

@Serializable
data class DailyTotalsResponse(
    val date: String,
    val totalCalories: Double = 0.0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0,
    val totalFiber: Double = 0.0,
    val mealsRegistered: Int = 0
)

class PlanRepository(private val apiClient: ApiClient) {

    suspend fun getActivePlan(patientId: String): Result<PlanDetailResponse?> {
        return try {
            val response = apiClient.httpClient.get("/patients/$patientId/plans/active")
                .body<ApiWrapper<PlanDetailResponse>>()
            if (response.success) Result.success(response.data)
            else Result.failure(Exception(response.error ?: "Failed to load plan"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlanDetail(planId: String): Result<PlanDetailResponse> {
        return try {
            val response = apiClient.httpClient.get("/plans/$planId")
                .body<ApiWrapper<PlanDetailResponse>>()
            if (response.success && response.data != null) Result.success(response.data)
            else Result.failure(Exception(response.error ?: "Failed to load plan"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyTotals(patientId: String, date: String): Result<DailyTotalsResponse> {
        return try {
            val response = apiClient.httpClient.get("/consumption/daily-totals") {
                parameter("patientId", patientId)
                parameter("date", date)
            }.body<ApiWrapper<DailyTotalsResponse>>()
            if (response.success && response.data != null) Result.success(response.data)
            else Result.failure(Exception(response.error ?: "Failed to load totals"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
