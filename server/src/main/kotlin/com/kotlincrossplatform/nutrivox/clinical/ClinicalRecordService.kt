package com.kotlincrossplatform.nutrivox.clinical

import com.kotlincrossplatform.nutrivox.common.PaginatedResponse
import com.kotlincrossplatform.nutrivox.common.PaginationParams
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.util.*

@Serializable
data class ClinicalRecordDTO(
    val id: String,
    val patientId: String,
    val chiefComplaint: String? = null,
    val familyHistory: String? = null,
    val pathologies: String? = null,
    val intolerances: String? = null,
    val allergies: String? = null,
    val medications: String? = null,
    val supplementation: String? = null,
    val bowelHabits: String? = null,
    val sleepPattern: String? = null,
    val physicalActivity: String? = null,
    val waterIntake: String? = null,
    val foodPreferences: String? = null,
    val foodAversions: String? = null
)

@Serializable
data class UpdateClinicalRecordRequest(
    val chiefComplaint: String? = null,
    val familyHistory: String? = null,
    val pathologies: String? = null,
    val intolerances: String? = null,
    val allergies: String? = null,
    val medications: String? = null,
    val supplementation: String? = null,
    val bowelHabits: String? = null,
    val sleepPattern: String? = null,
    val physicalActivity: String? = null,
    val waterIntake: String? = null,
    val foodPreferences: String? = null,
    val foodAversions: String? = null
)

@Serializable
data class CreateEvolutionRequest(
    val date: String? = null,
    val generalNotes: String? = null,
    val planAdherence: String? = null,
    val complications: String? = null,
    val reportedSymptoms: String? = null,
    val adjustments: String? = null,
    val recommendations: String? = null
)

@Serializable
data class EvolutionSummary(
    val id: String,
    val date: String,
    val generalNotes: String? = null,
    val adjustments: String? = null
)

class ClinicalRecordService {

    fun getOrCreateRecord(patientId: UUID): ClinicalRecordDTO = transaction {
        val existing = ClinicalRecordTable.selectAll()
            .where { ClinicalRecordTable.patientId eq patientId }
            .singleOrNull()

        if (existing != null) {
            existing.toClinicalRecordDTO()
        } else {
            val now = OffsetDateTime.now()
            val id = UUID.randomUUID()
            ClinicalRecordTable.insert {
                it[ClinicalRecordTable.id] = id
                it[ClinicalRecordTable.patientId] = patientId
                it[createdAt] = now
                it[updatedAt] = now
            }
            ClinicalRecordDTO(
                id = id.toString(),
                patientId = patientId.toString()
            )
        }
    }

    fun updateRecord(patientId: UUID, request: UpdateClinicalRecordRequest): ClinicalRecordDTO = transaction {
        // Ensure record exists
        val record = ClinicalRecordTable.selectAll()
            .where { ClinicalRecordTable.patientId eq patientId }
            .singleOrNull()

        val now = OffsetDateTime.now()

        if (record == null) {
            val id = UUID.randomUUID()
            ClinicalRecordTable.insert {
                it[ClinicalRecordTable.id] = id
                it[ClinicalRecordTable.patientId] = patientId
                it[chiefComplaint] = request.chiefComplaint
                it[familyHistory] = request.familyHistory
                it[pathologies] = request.pathologies
                it[intolerances] = request.intolerances
                it[allergies] = request.allergies
                it[medications] = request.medications
                it[supplementation] = request.supplementation
                it[bowelHabits] = request.bowelHabits
                it[sleepPattern] = request.sleepPattern
                it[physicalActivity] = request.physicalActivity
                it[waterIntake] = request.waterIntake
                it[foodPreferences] = request.foodPreferences
                it[foodAversions] = request.foodAversions
                it[createdAt] = now
                it[updatedAt] = now
            }
        } else {
            ClinicalRecordTable.update({ ClinicalRecordTable.patientId eq patientId }) {
                it[chiefComplaint] = request.chiefComplaint
                it[familyHistory] = request.familyHistory
                it[pathologies] = request.pathologies
                it[intolerances] = request.intolerances
                it[allergies] = request.allergies
                it[medications] = request.medications
                it[supplementation] = request.supplementation
                it[bowelHabits] = request.bowelHabits
                it[sleepPattern] = request.sleepPattern
                it[physicalActivity] = request.physicalActivity
                it[waterIntake] = request.waterIntake
                it[foodPreferences] = request.foodPreferences
                it[foodAversions] = request.foodAversions
                it[updatedAt] = now
            }
        }

        ClinicalRecordTable.selectAll()
            .where { ClinicalRecordTable.patientId eq patientId }
            .single()
            .toClinicalRecordDTO()
    }

    fun addEvolution(patientId: UUID, nutritionistId: UUID, request: CreateEvolutionRequest): UUID = transaction {
        val evolutionDate = if (request.date != null) {
            kotlinx.datetime.LocalDate.parse(request.date)
        } else {
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        }

        val id = UUID.randomUUID()
        ClinicalEvolutionTable.insert {
            it[ClinicalEvolutionTable.id] = id
            it[ClinicalEvolutionTable.patientId] = patientId
            it[ClinicalEvolutionTable.nutritionistId] = nutritionistId
            it[date] = evolutionDate
            it[generalNotes] = request.generalNotes
            it[planAdherence] = request.planAdherence
            it[complications] = request.complications
            it[reportedSymptoms] = request.reportedSymptoms
            it[adjustments] = request.adjustments
            it[recommendations] = request.recommendations
            it[createdAt] = OffsetDateTime.now()
        }
        id
    }

    fun listEvolutions(patientId: UUID, pagination: PaginationParams): PaginatedResponse<EvolutionSummary> = transaction {
        val baseQuery = ClinicalEvolutionTable.selectAll()
            .where { ClinicalEvolutionTable.patientId eq patientId }

        val total = baseQuery.count()
        val items = baseQuery
            .orderBy(ClinicalEvolutionTable.date, SortOrder.DESC)
            .limit(pagination.pageSize)
            .offset(pagination.offset.toLong())
            .map { row ->
                val notes = row[ClinicalEvolutionTable.generalNotes]
                EvolutionSummary(
                    id = row[ClinicalEvolutionTable.id].toString(),
                    date = row[ClinicalEvolutionTable.date].toString(),
                    generalNotes = notes?.take(200),
                    adjustments = row[ClinicalEvolutionTable.adjustments]
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

    private fun ResultRow.toClinicalRecordDTO() = ClinicalRecordDTO(
        id = this[ClinicalRecordTable.id].toString(),
        patientId = this[ClinicalRecordTable.patientId].toString(),
        chiefComplaint = this[ClinicalRecordTable.chiefComplaint],
        familyHistory = this[ClinicalRecordTable.familyHistory],
        pathologies = this[ClinicalRecordTable.pathologies],
        intolerances = this[ClinicalRecordTable.intolerances],
        allergies = this[ClinicalRecordTable.allergies],
        medications = this[ClinicalRecordTable.medications],
        supplementation = this[ClinicalRecordTable.supplementation],
        bowelHabits = this[ClinicalRecordTable.bowelHabits],
        sleepPattern = this[ClinicalRecordTable.sleepPattern],
        physicalActivity = this[ClinicalRecordTable.physicalActivity],
        waterIntake = this[ClinicalRecordTable.waterIntake],
        foodPreferences = this[ClinicalRecordTable.foodPreferences],
        foodAversions = this[ClinicalRecordTable.foodAversions]
    )
}
