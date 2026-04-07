package com.kotlincrossplatform.nutrivox.ui.patient.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage
import com.kotlincrossplatform.nutrivox.data.repository.DailyTotalsResponse
import com.kotlincrossplatform.nutrivox.data.repository.PlanDetailResponse
import com.kotlincrossplatform.nutrivox.data.repository.PlanRepository
import com.kotlincrossplatform.nutrivox.data.repository.VariationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PatientHomeViewModel(private val planRepository: PlanRepository) {
    var plan by mutableStateOf<PlanDetailResponse?>(null)
        private set
    var dailyTotals by mutableStateOf<DailyTotalsResponse?>(null)
        private set
    var selectedVariationId by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    private val scope = CoroutineScope(Dispatchers.Default)

    val selectedVariation: VariationResponse?
        get() = plan?.variations?.find { it.id == selectedVariationId }
            ?: plan?.variations?.find { it.isDefault }
            ?: plan?.variations?.firstOrNull()

    fun loadData() {
        val patientId = TokenStorage.userId ?: return
        isLoading = true
        error = null
        scope.launch {
            val planResult = planRepository.getActivePlan(patientId)
            planResult.fold(
                onSuccess = { p ->
                    plan = p
                    selectedVariationId = p?.variations?.find { it.isDefault }?.id
                        ?: p?.variations?.firstOrNull()?.id
                },
                onFailure = { error = it.message }
            )

            // Load daily totals for today
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.toString()
            val totalsResult = planRepository.getDailyTotals(patientId, today)
            totalsResult.fold(
                onSuccess = { dailyTotals = it },
                onFailure = { /* non-critical, totals just stay null */ }
            )

            isLoading = false
        }
    }

    fun selectVariation(variationId: String) {
        selectedVariationId = variationId
    }
}
