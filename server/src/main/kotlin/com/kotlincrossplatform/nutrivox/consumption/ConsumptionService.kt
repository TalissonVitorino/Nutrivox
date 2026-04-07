package com.kotlincrossplatform.nutrivox.consumption

import com.kotlincrossplatform.nutrivox.common.Exceptions.NotFoundException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ValidationException
import com.kotlincrossplatform.nutrivox.common.PaginatedResponse
import com.kotlincrossplatform.nutrivox.common.PaginationParams
import com.kotlincrossplatform.nutrivox.patients.PatientTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

// ── Request DTOs ──────────────────────────────────────────────────────

@Serializable
data class CreateConsumptionRequest(
    val mealId: String? = null,
    val variationId: String? = null,
    val date: String, // ISO date
    val time: String? = null, // HH:mm
    val mode: String, // full, partial, with_substitution, off_plan
    val notes: String? = null,
    val items: List<CreateConsumptionItemRequest>
)

@Serializable
data class CreateConsumptionItemRequest(
    val mealItemId: String? = null,
    val foodId: String? = null,
    val foodName: String,
    val quantityGrams: Double? = null,
    val householdMeasure: String? = null,
    val wasConsumed: Boolean = true,
    val isSubstitution: Boolean = false,
    val substitutionOrigin: String? = null, // authorized, ai_suggestion
    val originalFoodName: String? = null,
    val isOffPlan: Boolean = false,
    val calories: Double? = null,
    val proteinG: Double? = null,
    val carbsG: Double? = null,
    val fatG: Double? = null,
    val fiberG: Double? = null
)

// ── Response DTOs ─────────────────────────────────────────────────────

@Serializable
data class ConsumptionRecordDTO(
    val id: String,
    val mealId: String?,
    val date: String,
    val time: String?,
    val mode: String,
    val notes: String?,
    val items: List<ConsumptionItemDTO>
)

@Serializable
data class ConsumptionItemDTO(
    val id: String,
    val foodName: String,
    val quantityGrams: Double?,
    val householdMeasure: String?,
    val wasConsumed: Boolean,
    val isSubstitution: Boolean,
    val substitutionOrigin: String?,
    val originalFoodName: String?,
    val isOffPlan: Boolean,
    val calories: Double?,
    val proteinG: Double?,
    val carbsG: Double?,
    val fatG: Double?,
    val fiberG: Double?
)

@Serializable
data class DailyTotalsDTO(
    val date: String,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double,
    val totalFiber: Double,
    val mealsRegistered: Int
)

// ── Service ───────────────────────────────────────────────────────────

class ConsumptionService {

    private fun now(): OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)

    /** Resolve a user's patient ID from their auth userId. */
    fun resolvePatientId(userId: UUID): UUID = transaction {
        PatientTable.selectAll().where { PatientTable.userId eq userId }
            .singleOrNull()?.get(PatientTable.id)
            ?: throw NotFoundException("Patient profile not found for this user")
    }

    // ── Register ──────────────────────────────────────────────────────

    fun registerConsumption(patientId: UUID, request: CreateConsumptionRequest): UUID = transaction {
        val validModes = setOf("full", "partial", "with_substitution", "off_plan")
        if (request.mode !in validModes) {
            throw ValidationException("Invalid mode '${request.mode}'. Must be one of: $validModes")
        }
        if (request.items.isEmpty()) {
            throw ValidationException("At least one consumption item is required")
        }

        val parsedDate = kotlinx.datetime.LocalDate.parse(request.date)
        val ts = now()

        val recordId = ConsumptionRecordTable.insert {
            it[ConsumptionRecordTable.patientId] = patientId
            it[mealId] = request.mealId?.let { m -> UUID.fromString(m) }
            it[variationId] = request.variationId?.let { v -> UUID.fromString(v) }
            it[date] = parsedDate
            it[time] = request.time
            it[mode] = request.mode
            it[notes] = request.notes
            it[createdAt] = ts
            it[updatedAt] = ts
        } get ConsumptionRecordTable.id

        for (item in request.items) {
            ConsumptionItemTable.insert {
                it[ConsumptionItemTable.recordId] = recordId
                it[mealItemId] = item.mealItemId?.let { m -> UUID.fromString(m) }
                it[foodId] = item.foodId?.let { f -> UUID.fromString(f) }
                it[foodName] = item.foodName
                it[quantityGrams] = item.quantityGrams?.let { v -> BigDecimal.valueOf(v) }
                it[householdMeasure] = item.householdMeasure
                it[wasConsumed] = item.wasConsumed
                it[isSubstitution] = item.isSubstitution
                it[substitutionOrigin] = item.substitutionOrigin
                it[originalFoodName] = item.originalFoodName
                it[isOffPlan] = item.isOffPlan
                it[calories] = item.calories?.let { v -> BigDecimal.valueOf(v) }
                it[proteinG] = item.proteinG?.let { v -> BigDecimal.valueOf(v) }
                it[carbsG] = item.carbsG?.let { v -> BigDecimal.valueOf(v) }
                it[fatG] = item.fatG?.let { v -> BigDecimal.valueOf(v) }
                it[fiberG] = item.fiberG?.let { v -> BigDecimal.valueOf(v) }
                it[createdAt] = ts
            }
        }

        recordId
    }

    // ── History ───────────────────────────────────────────────────────

    fun getConsumptionHistory(
        patientId: UUID,
        date: String?,
        pagination: PaginationParams
    ): PaginatedResponse<ConsumptionRecordDTO> = transaction {
        val baseCondition: Op<Boolean> = if (date != null) {
            val parsedDate = kotlinx.datetime.LocalDate.parse(date)
            (ConsumptionRecordTable.patientId eq patientId) and (ConsumptionRecordTable.date eq parsedDate)
        } else {
            ConsumptionRecordTable.patientId eq patientId
        }

        val total = ConsumptionRecordTable.selectAll().where { baseCondition }.count()

        val recordRows = ConsumptionRecordTable.selectAll()
            .where { baseCondition }
            .orderBy(ConsumptionRecordTable.date, SortOrder.DESC)
            .orderBy(ConsumptionRecordTable.createdAt, SortOrder.DESC)
            .limit(pagination.pageSize)
            .offset(pagination.offset.toLong())
            .toList()

        val recordIds = recordRows.map { it[ConsumptionRecordTable.id] }

        val itemRows = if (recordIds.isNotEmpty()) {
            ConsumptionItemTable.selectAll()
                .where { ConsumptionItemTable.recordId inList recordIds }
                .toList()
        } else emptyList()

        val itemsByRecord = itemRows.groupBy { it[ConsumptionItemTable.recordId] }

        val items = recordRows.map { row ->
            val rId = row[ConsumptionRecordTable.id]
            val consumptionItems = (itemsByRecord[rId] ?: emptyList()).map { ir ->
                ConsumptionItemDTO(
                    id = ir[ConsumptionItemTable.id].toString(),
                    foodName = ir[ConsumptionItemTable.foodName],
                    quantityGrams = ir[ConsumptionItemTable.quantityGrams]?.toDouble(),
                    householdMeasure = ir[ConsumptionItemTable.householdMeasure],
                    wasConsumed = ir[ConsumptionItemTable.wasConsumed],
                    isSubstitution = ir[ConsumptionItemTable.isSubstitution],
                    substitutionOrigin = ir[ConsumptionItemTable.substitutionOrigin],
                    originalFoodName = ir[ConsumptionItemTable.originalFoodName],
                    isOffPlan = ir[ConsumptionItemTable.isOffPlan],
                    calories = ir[ConsumptionItemTable.calories]?.toDouble(),
                    proteinG = ir[ConsumptionItemTable.proteinG]?.toDouble(),
                    carbsG = ir[ConsumptionItemTable.carbsG]?.toDouble(),
                    fatG = ir[ConsumptionItemTable.fatG]?.toDouble(),
                    fiberG = ir[ConsumptionItemTable.fiberG]?.toDouble()
                )
            }
            ConsumptionRecordDTO(
                id = rId.toString(),
                mealId = row[ConsumptionRecordTable.mealId]?.toString(),
                date = row[ConsumptionRecordTable.date].toString(),
                time = row[ConsumptionRecordTable.time],
                mode = row[ConsumptionRecordTable.mode],
                notes = row[ConsumptionRecordTable.notes],
                items = consumptionItems
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

    // ── Daily Totals ─────────────────────────────────────────────────

    fun getDailyTotals(patientId: UUID, date: String): DailyTotalsDTO = transaction {
        val parsedDate = kotlinx.datetime.LocalDate.parse(date)

        // Get all records for this patient on this date
        val recordRows = ConsumptionRecordTable.selectAll().where {
            (ConsumptionRecordTable.patientId eq patientId) and (ConsumptionRecordTable.date eq parsedDate)
        }.toList()

        val recordIds = recordRows.map { it[ConsumptionRecordTable.id] }

        // Get all consumed items (wasConsumed = true) for these records
        val consumedItems = if (recordIds.isNotEmpty()) {
            ConsumptionItemTable.selectAll().where {
                (ConsumptionItemTable.recordId inList recordIds) and
                    (ConsumptionItemTable.wasConsumed eq true)
            }.toList()
        } else emptyList()

        DailyTotalsDTO(
            date = date,
            totalCalories = consumedItems.sumOf { it[ConsumptionItemTable.calories]?.toDouble() ?: 0.0 },
            totalProtein = consumedItems.sumOf { it[ConsumptionItemTable.proteinG]?.toDouble() ?: 0.0 },
            totalCarbs = consumedItems.sumOf { it[ConsumptionItemTable.carbsG]?.toDouble() ?: 0.0 },
            totalFat = consumedItems.sumOf { it[ConsumptionItemTable.fatG]?.toDouble() ?: 0.0 },
            totalFiber = consumedItems.sumOf { it[ConsumptionItemTable.fiberG]?.toDouble() ?: 0.0 },
            mealsRegistered = recordRows.size
        )
    }

    // ── Delete (same-day only) ───────────────────────────────────────

    fun deleteConsumption(recordId: UUID, patientId: UUID): Unit = transaction {
        val record = ConsumptionRecordTable.selectAll().where {
            (ConsumptionRecordTable.id eq recordId) and (ConsumptionRecordTable.patientId eq patientId)
        }.singleOrNull() ?: throw NotFoundException("Consumption record not found")

        val recordDate = record[ConsumptionRecordTable.date]
        val today = java.time.LocalDate.now(java.time.ZoneOffset.UTC).let {
            kotlinx.datetime.LocalDate(it.year, it.monthValue, it.dayOfMonth)
        }

        if (recordDate != today) {
            throw ValidationException("Only same-day consumption records can be deleted")
        }

        // Delete items first, then the record
        ConsumptionItemTable.deleteWhere { ConsumptionItemTable.recordId eq recordId }
        ConsumptionRecordTable.deleteWhere { ConsumptionRecordTable.id eq recordId }
    }
}
