package com.kotlincrossplatform.nutrivox.plans

import com.kotlincrossplatform.nutrivox.common.Exceptions.NotFoundException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ValidationException
import com.kotlincrossplatform.nutrivox.common.PaginatedResponse
import com.kotlincrossplatform.nutrivox.common.PaginationParams
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

// ── Response DTOs ──────────────────────────────────────────────────────

@Serializable
data class PlanSummary(
    val id: String,
    val name: String,
    val status: String,
    val objective: String?,
    val startDate: String?,
    val goalCalories: Double?
)

@Serializable
data class PlanDetailDTO(
    val id: String,
    val name: String,
    val status: String,
    val objective: String?,
    val startDate: String?,
    val endDate: String?,
    val generalNotes: String?,
    val goalCalories: Double?,
    val goalProteinG: Double?,
    val goalCarbsG: Double?,
    val goalFatG: Double?,
    val goalFiberG: Double?,
    val variations: List<VariationDTO>
)

@Serializable
data class VariationDTO(
    val id: String,
    val name: String,
    val isDefault: Boolean,
    val isPatientAccessible: Boolean,
    val sortOrder: Int,
    val meals: List<MealDTO>
)

@Serializable
data class MealDTO(
    val id: String,
    val name: String,
    val suggestedTime: String?,
    val sortOrder: Int,
    val notes: String?,
    val items: List<MealItemDTO>,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double
)

@Serializable
data class MealItemDTO(
    val id: String,
    val foodId: String?,
    val foodName: String,
    val quantityGrams: Double?,
    val householdMeasure: String?,
    val isAdLibitum: Boolean,
    val notes: String?,
    val calories: Double?,
    val proteinG: Double?,
    val carbsG: Double?,
    val fatG: Double?,
    val fiberG: Double?,
    val substitutions: List<SubstitutionDTO>
)

@Serializable
data class SubstitutionDTO(
    val id: String,
    val foodId: String?,
    val foodName: String,
    val quantityGrams: Double?,
    val householdMeasure: String?,
    val calories: Double?,
    val proteinG: Double?,
    val carbsG: Double?,
    val fatG: Double?,
    val notes: String?
)

// ── Request DTOs ───────────────────────────────────────────────────────

@Serializable
data class CreatePlanRequest(
    val name: String,
    val objective: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val generalNotes: String? = null,
    val goalCalories: Double? = null,
    val goalProteinG: Double? = null,
    val goalCarbsG: Double? = null,
    val goalFatG: Double? = null,
    val goalFiberG: Double? = null,
    val variations: List<CreateVariationRequest> = emptyList()
)

@Serializable
data class CreateVariationRequest(
    val name: String,
    val isDefault: Boolean = false,
    val isPatientAccessible: Boolean = true,
    val sortOrder: Int = 0,
    val meals: List<CreateMealRequest> = emptyList()
)

@Serializable
data class CreateMealRequest(
    val name: String,
    val suggestedTime: String? = null,
    val sortOrder: Int = 0,
    val notes: String? = null,
    val items: List<CreateMealItemRequest> = emptyList()
)

@Serializable
data class CreateMealItemRequest(
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
    val substitutions: List<CreateSubstitutionRequest> = emptyList()
)

@Serializable
data class CreateSubstitutionRequest(
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

// ── Service ────────────────────────────────────────────────────────────

class PlanService {

    private fun now(): OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)

    // ── Create ─────────────────────────────────────────────────────────

    fun createPlan(patientId: UUID, nutritionistId: UUID, request: CreatePlanRequest): UUID = transaction {
        val planId = MealPlanTable.insert {
            it[MealPlanTable.patientId] = patientId
            it[MealPlanTable.nutritionistId] = nutritionistId
            it[name] = request.name
            it[objective] = request.objective
            it[status] = "draft"
            it[startDate] = request.startDate?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[endDate] = request.endDate?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[generalNotes] = request.generalNotes
            it[goalCalories] = request.goalCalories?.let { v -> BigDecimal.valueOf(v) }
            it[goalProteinG] = request.goalProteinG?.let { v -> BigDecimal.valueOf(v) }
            it[goalCarbsG] = request.goalCarbsG?.let { v -> BigDecimal.valueOf(v) }
            it[goalFatG] = request.goalFatG?.let { v -> BigDecimal.valueOf(v) }
            it[goalFiberG] = request.goalFiberG?.let { v -> BigDecimal.valueOf(v) }
            it[createdAt] = now()
            it[updatedAt] = now()
        } get MealPlanTable.id

        insertVariations(planId, request.variations)
        planId
    }

    private fun insertVariations(planId: UUID, variations: List<CreateVariationRequest>) {
        for (v in variations) {
            val varId = DietVariationTable.insert {
                it[DietVariationTable.planId] = planId
                it[name] = v.name
                it[isDefault] = v.isDefault
                it[isPatientAccessible] = v.isPatientAccessible
                it[sortOrder] = v.sortOrder
                it[createdAt] = now()
            } get DietVariationTable.id

            insertMeals(varId, v.meals)
        }
    }

    private fun insertMeals(variationId: UUID, meals: List<CreateMealRequest>) {
        for (m in meals) {
            val mealId = MealTable.insert {
                it[MealTable.variationId] = variationId
                it[name] = m.name
                it[suggestedTime] = m.suggestedTime
                it[sortOrder] = m.sortOrder
                it[notes] = m.notes
                it[createdAt] = now()
            } get MealTable.id

            insertItems(mealId, m.items)
        }
    }

    private fun insertItems(mealId: UUID, items: List<CreateMealItemRequest>) {
        for ((index, item) in items.withIndex()) {
            val itemId = MealItemTable.insert {
                it[MealItemTable.mealId] = mealId
                it[foodId] = item.foodId?.let { f -> UUID.fromString(f) }
                it[foodName] = item.foodName
                it[quantityGrams] = item.quantityGrams?.let { v -> BigDecimal.valueOf(v) }
                it[householdMeasure] = item.householdMeasure
                it[isAdLibitum] = item.isAdLibitum
                it[notes] = item.notes
                it[calories] = item.calories?.let { v -> BigDecimal.valueOf(v) }
                it[proteinG] = item.proteinG?.let { v -> BigDecimal.valueOf(v) }
                it[carbsG] = item.carbsG?.let { v -> BigDecimal.valueOf(v) }
                it[fatG] = item.fatG?.let { v -> BigDecimal.valueOf(v) }
                it[fiberG] = item.fiberG?.let { v -> BigDecimal.valueOf(v) }
                it[sortOrder] = index
                it[createdAt] = now()
            } get MealItemTable.id

            insertSubstitutions(itemId, item.substitutions)
        }
    }

    private fun insertSubstitutions(mealItemId: UUID, subs: List<CreateSubstitutionRequest>) {
        for (s in subs) {
            AuthorizedSubstitutionTable.insert {
                it[AuthorizedSubstitutionTable.mealItemId] = mealItemId
                it[foodId] = s.foodId?.let { f -> UUID.fromString(f) }
                it[foodName] = s.foodName
                it[quantityGrams] = s.quantityGrams?.let { v -> BigDecimal.valueOf(v) }
                it[householdMeasure] = s.householdMeasure
                it[calories] = s.calories?.let { v -> BigDecimal.valueOf(v) }
                it[proteinG] = s.proteinG?.let { v -> BigDecimal.valueOf(v) }
                it[carbsG] = s.carbsG?.let { v -> BigDecimal.valueOf(v) }
                it[fatG] = s.fatG?.let { v -> BigDecimal.valueOf(v) }
                it[notes] = s.notes
            }
        }
    }

    // ── Read ───────────────────────────────────────────────────────────

    fun getPlanDetail(planId: UUID): PlanDetailDTO = transaction {
        val planRow = MealPlanTable.selectAll().where { MealPlanTable.id eq planId }
            .singleOrNull() ?: throw NotFoundException("Plan not found")

        buildDetailDTO(planRow)
    }

    fun getActivePlan(patientId: UUID): PlanDetailDTO? = transaction {
        val planRow = MealPlanTable.selectAll().where {
            (MealPlanTable.patientId eq patientId) and (MealPlanTable.status eq "active")
        }.singleOrNull() ?: return@transaction null

        buildDetailDTO(planRow)
    }

    private fun buildDetailDTO(planRow: ResultRow): PlanDetailDTO {
        val planId = planRow[MealPlanTable.id]

        // Load variations
        val variationRows = DietVariationTable.selectAll()
            .where { DietVariationTable.planId eq planId }
            .orderBy(DietVariationTable.sortOrder)
            .toList()

        val variationIds = variationRows.map { it[DietVariationTable.id] }

        // Load all meals for these variations
        val mealRows = if (variationIds.isNotEmpty()) {
            MealTable.selectAll()
                .where { MealTable.variationId inList variationIds }
                .orderBy(MealTable.sortOrder)
                .toList()
        } else emptyList()

        val mealIds = mealRows.map { it[MealTable.id] }

        // Load all items for these meals
        val itemRows = if (mealIds.isNotEmpty()) {
            MealItemTable.selectAll()
                .where { MealItemTable.mealId inList mealIds }
                .orderBy(MealItemTable.sortOrder)
                .toList()
        } else emptyList()

        val itemIds = itemRows.map { it[MealItemTable.id] }

        // Load all substitutions
        val subRows = if (itemIds.isNotEmpty()) {
            AuthorizedSubstitutionTable.selectAll()
                .where { AuthorizedSubstitutionTable.mealItemId inList itemIds }
                .toList()
        } else emptyList()

        // Group substitutions by meal item
        val subsByItem = subRows.groupBy { it[AuthorizedSubstitutionTable.mealItemId] }

        // Build item DTOs grouped by meal
        val itemsByMeal = itemRows.groupBy { it[MealItemTable.mealId] }

        // Build meal DTOs grouped by variation
        val mealsByVariation = mealRows.groupBy { it[MealTable.variationId] }

        val variations = variationRows.map { vr ->
            val varId = vr[DietVariationTable.id]
            val meals = (mealsByVariation[varId] ?: emptyList()).map { mr ->
                val mealId = mr[MealTable.id]
                val items = (itemsByMeal[mealId] ?: emptyList()).map { ir ->
                    val iId = ir[MealItemTable.id]
                    val subs = (subsByItem[iId] ?: emptyList()).map { sr ->
                        SubstitutionDTO(
                            id = sr[AuthorizedSubstitutionTable.id].toString(),
                            foodId = sr[AuthorizedSubstitutionTable.foodId]?.toString(),
                            foodName = sr[AuthorizedSubstitutionTable.foodName],
                            quantityGrams = sr[AuthorizedSubstitutionTable.quantityGrams]?.toDouble(),
                            householdMeasure = sr[AuthorizedSubstitutionTable.householdMeasure],
                            calories = sr[AuthorizedSubstitutionTable.calories]?.toDouble(),
                            proteinG = sr[AuthorizedSubstitutionTable.proteinG]?.toDouble(),
                            carbsG = sr[AuthorizedSubstitutionTable.carbsG]?.toDouble(),
                            fatG = sr[AuthorizedSubstitutionTable.fatG]?.toDouble(),
                            notes = sr[AuthorizedSubstitutionTable.notes]
                        )
                    }
                    MealItemDTO(
                        id = iId.toString(),
                        foodId = ir[MealItemTable.foodId]?.toString(),
                        foodName = ir[MealItemTable.foodName],
                        quantityGrams = ir[MealItemTable.quantityGrams]?.toDouble(),
                        householdMeasure = ir[MealItemTable.householdMeasure],
                        isAdLibitum = ir[MealItemTable.isAdLibitum],
                        notes = ir[MealItemTable.notes],
                        calories = ir[MealItemTable.calories]?.toDouble(),
                        proteinG = ir[MealItemTable.proteinG]?.toDouble(),
                        carbsG = ir[MealItemTable.carbsG]?.toDouble(),
                        fatG = ir[MealItemTable.fatG]?.toDouble(),
                        fiberG = ir[MealItemTable.fiberG]?.toDouble(),
                        substitutions = subs
                    )
                }

                // Compute meal totals (skip ad libitum items)
                val countableItems = items.filter { !it.isAdLibitum }
                MealDTO(
                    id = mealId.toString(),
                    name = mr[MealTable.name],
                    suggestedTime = mr[MealTable.suggestedTime],
                    sortOrder = mr[MealTable.sortOrder],
                    notes = mr[MealTable.notes],
                    items = items,
                    totalCalories = countableItems.sumOf { it.calories ?: 0.0 },
                    totalProtein = countableItems.sumOf { it.proteinG ?: 0.0 },
                    totalCarbs = countableItems.sumOf { it.carbsG ?: 0.0 },
                    totalFat = countableItems.sumOf { it.fatG ?: 0.0 }
                )
            }

            VariationDTO(
                id = varId.toString(),
                name = vr[DietVariationTable.name],
                isDefault = vr[DietVariationTable.isDefault],
                isPatientAccessible = vr[DietVariationTable.isPatientAccessible],
                sortOrder = vr[DietVariationTable.sortOrder],
                meals = meals
            )
        }

        return PlanDetailDTO(
            id = planRow[MealPlanTable.id].toString(),
            name = planRow[MealPlanTable.name],
            status = planRow[MealPlanTable.status],
            objective = planRow[MealPlanTable.objective],
            startDate = planRow[MealPlanTable.startDate]?.toString(),
            endDate = planRow[MealPlanTable.endDate]?.toString(),
            generalNotes = planRow[MealPlanTable.generalNotes],
            goalCalories = planRow[MealPlanTable.goalCalories]?.toDouble(),
            goalProteinG = planRow[MealPlanTable.goalProteinG]?.toDouble(),
            goalCarbsG = planRow[MealPlanTable.goalCarbsG]?.toDouble(),
            goalFatG = planRow[MealPlanTable.goalFatG]?.toDouble(),
            goalFiberG = planRow[MealPlanTable.goalFiberG]?.toDouble(),
            variations = variations
        )
    }

    // ── List ───────────────────────────────────────────────────────────

    fun listPatientPlans(patientId: UUID, pagination: PaginationParams): PaginatedResponse<PlanSummary> = transaction {
        val baseQuery = MealPlanTable.selectAll().where { MealPlanTable.patientId eq patientId }

        val total = baseQuery.count()
        val items = baseQuery
            .orderBy(MealPlanTable.createdAt, SortOrder.DESC)
            .limit(pagination.pageSize)
            .offset(pagination.offset.toLong())
            .map { row ->
                PlanSummary(
                    id = row[MealPlanTable.id].toString(),
                    name = row[MealPlanTable.name],
                    status = row[MealPlanTable.status],
                    objective = row[MealPlanTable.objective],
                    startDate = row[MealPlanTable.startDate]?.toString(),
                    goalCalories = row[MealPlanTable.goalCalories]?.toDouble()
                )
            }

        PaginatedResponse(
            items = items,
            total = total,
            page = pagination.page,
            pageSize = pagination.pageSize,
            totalPages = ((total + pagination.pageSize - 1) / pagination.pageSize).toInt()
        )
    }

    // ── Update (draft only) ────────────────────────────────────────────

    fun updatePlan(planId: UUID, request: CreatePlanRequest): Unit = transaction {
        val planRow = MealPlanTable.selectAll().where { MealPlanTable.id eq planId }
            .singleOrNull() ?: throw NotFoundException("Plan not found")

        if (planRow[MealPlanTable.status] != "draft") {
            throw ValidationException("Only draft plans can be edited")
        }

        // Update plan fields
        MealPlanTable.update({ MealPlanTable.id eq planId }) {
            it[name] = request.name
            it[objective] = request.objective
            it[startDate] = request.startDate?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[endDate] = request.endDate?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[generalNotes] = request.generalNotes
            it[goalCalories] = request.goalCalories?.let { v -> BigDecimal.valueOf(v) }
            it[goalProteinG] = request.goalProteinG?.let { v -> BigDecimal.valueOf(v) }
            it[goalCarbsG] = request.goalCarbsG?.let { v -> BigDecimal.valueOf(v) }
            it[goalFatG] = request.goalFatG?.let { v -> BigDecimal.valueOf(v) }
            it[goalFiberG] = request.goalFiberG?.let { v -> BigDecimal.valueOf(v) }
            it[updatedAt] = now()
        }

        // Delete existing nested structure
        deleteNestedStructure(planId)

        // Re-create nested structure
        insertVariations(planId, request.variations)
    }

    private fun deleteNestedStructure(planId: UUID) {
        val variationIds = DietVariationTable.selectAll()
            .where { DietVariationTable.planId eq planId }
            .map { it[DietVariationTable.id] }

        if (variationIds.isEmpty()) return

        val mealIds = MealTable.selectAll()
            .where { MealTable.variationId inList variationIds }
            .map { it[MealTable.id] }

        if (mealIds.isNotEmpty()) {
            val itemIds = MealItemTable.selectAll()
                .where { MealItemTable.mealId inList mealIds }
                .map { it[MealItemTable.id] }

            if (itemIds.isNotEmpty()) {
                AuthorizedSubstitutionTable.deleteWhere {
                    SqlExpressionBuilder.run { AuthorizedSubstitutionTable.mealItemId inList itemIds }
                }
            }
            MealItemTable.deleteWhere {
                SqlExpressionBuilder.run { MealItemTable.mealId inList mealIds }
            }
        }
        MealTable.deleteWhere {
            SqlExpressionBuilder.run { MealTable.variationId inList variationIds }
        }
        DietVariationTable.deleteWhere { DietVariationTable.planId eq planId }
    }

    // ── Activate ───────────────────────────────────────────────────────

    fun activatePlan(planId: UUID, nutritionistId: UUID): Unit = transaction {
        val planRow = MealPlanTable.selectAll().where { MealPlanTable.id eq planId }
            .singleOrNull() ?: throw NotFoundException("Plan not found")

        // Validate plan has at least one variation with meals and items
        val variationIds = DietVariationTable.selectAll()
            .where { DietVariationTable.planId eq planId }
            .map { it[DietVariationTable.id] }

        if (variationIds.isEmpty()) {
            throw ValidationException("Plan must have at least one diet variation")
        }

        val mealIds = MealTable.selectAll()
            .where { MealTable.variationId inList variationIds }
            .map { it[MealTable.id] }

        if (mealIds.isEmpty()) {
            throw ValidationException("Plan must have at least one meal")
        }

        val itemCount = MealItemTable.selectAll()
            .where { MealItemTable.mealId inList mealIds }
            .count()

        if (itemCount == 0L) {
            throw ValidationException("Plan must have at least one meal item")
        }

        // Deactivate any current active plan for this patient
        val patientId = planRow[MealPlanTable.patientId]
        MealPlanTable.update({
            (MealPlanTable.patientId eq patientId) and (MealPlanTable.status eq "active")
        }) {
            it[status] = "replaced"
            it[updatedAt] = now()
        }

        // Activate this plan
        MealPlanTable.update({ MealPlanTable.id eq planId }) {
            it[status] = "active"
            it[updatedAt] = now()
        }
    }

    // ── Deactivate ─────────────────────────────────────────────────────

    fun deactivatePlan(planId: UUID): Unit = transaction {
        val updated = MealPlanTable.update({ MealPlanTable.id eq planId }) {
            it[status] = "inactive"
            it[updatedAt] = now()
        }
        if (updated == 0) throw NotFoundException("Plan not found")
    }

    // ── Duplicate ──────────────────────────────────────────────────────

    fun duplicatePlan(planId: UUID, nutritionistId: UUID): UUID = transaction {
        val detail = getPlanDetail(planId)

        val newPlanId = MealPlanTable.insert {
            it[patientId] = MealPlanTable.selectAll().where { MealPlanTable.id eq planId }
                .single()[MealPlanTable.patientId]
            it[MealPlanTable.nutritionistId] = nutritionistId
            it[name] = "${detail.name} (cópia)"
            it[objective] = detail.objective
            it[status] = "draft"
            it[startDate] = detail.startDate?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[endDate] = detail.endDate?.let { d -> kotlinx.datetime.LocalDate.parse(d) }
            it[generalNotes] = detail.generalNotes
            it[goalCalories] = detail.goalCalories?.let { v -> BigDecimal.valueOf(v) }
            it[goalProteinG] = detail.goalProteinG?.let { v -> BigDecimal.valueOf(v) }
            it[goalCarbsG] = detail.goalCarbsG?.let { v -> BigDecimal.valueOf(v) }
            it[goalFatG] = detail.goalFatG?.let { v -> BigDecimal.valueOf(v) }
            it[goalFiberG] = detail.goalFiberG?.let { v -> BigDecimal.valueOf(v) }
            it[createdAt] = now()
            it[updatedAt] = now()
        } get MealPlanTable.id

        // Deep copy variations
        val variationRequests = detail.variations.map { v ->
            CreateVariationRequest(
                name = v.name,
                isDefault = v.isDefault,
                isPatientAccessible = v.isPatientAccessible,
                sortOrder = v.sortOrder,
                meals = v.meals.map { m ->
                    CreateMealRequest(
                        name = m.name,
                        suggestedTime = m.suggestedTime,
                        sortOrder = m.sortOrder,
                        notes = m.notes,
                        items = m.items.map { i ->
                            CreateMealItemRequest(
                                foodId = i.foodId,
                                foodName = i.foodName,
                                quantityGrams = i.quantityGrams,
                                householdMeasure = i.householdMeasure,
                                isAdLibitum = i.isAdLibitum,
                                notes = i.notes,
                                calories = i.calories,
                                proteinG = i.proteinG,
                                carbsG = i.carbsG,
                                fatG = i.fatG,
                                fiberG = i.fiberG,
                                substitutions = i.substitutions.map { s ->
                                    CreateSubstitutionRequest(
                                        foodId = s.foodId,
                                        foodName = s.foodName,
                                        quantityGrams = s.quantityGrams,
                                        householdMeasure = s.householdMeasure,
                                        calories = s.calories,
                                        proteinG = s.proteinG,
                                        carbsG = s.carbsG,
                                        fatG = s.fatG,
                                        notes = s.notes
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }

        insertVariations(newPlanId, variationRequests)
        newPlanId
    }
}
