package com.kotlincrossplatform.nutrivox.data.repository

import com.kotlincrossplatform.nutrivox.data.remote.ApiClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class RegisterConsumptionRequest(
    val mealId: String? = null,
    val variationId: String? = null,
    val date: String,
    val time: String? = null,
    val mode: String,
    val notes: String? = null,
    val items: List<ConsumptionItemRequest>
)

@Serializable
data class ConsumptionItemRequest(
    val mealItemId: String? = null,
    val foodId: String? = null,
    val foodName: String,
    val quantityGrams: Double? = null,
    val householdMeasure: String? = null,
    val wasConsumed: Boolean = true,
    val isSubstitution: Boolean = false,
    val substitutionOrigin: String? = null,
    val originalFoodName: String? = null,
    val isOffPlan: Boolean = false,
    val calories: Double? = null,
    val proteinG: Double? = null,
    val carbsG: Double? = null,
    val fatG: Double? = null,
    val fiberG: Double? = null
)

class ConsumptionRepository(private val apiClient: ApiClient) {

    suspend fun registerConsumption(request: RegisterConsumptionRequest): Result<String> {
        return try {
            val response = apiClient.httpClient.post("/consumption") {
                setBody(request)
            }.body<ApiWrapper<Map<String, String>>>()
            if (response.success) Result.success(response.data?.get("id") ?: "")
            else Result.failure(Exception(response.error ?: "Failed to register consumption"))
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
            else Result.failure(Exception(response.error ?: "Failed to load daily totals"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
