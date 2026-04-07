package com.kotlincrossplatform.nutrivox.clinical

import com.kotlincrossplatform.nutrivox.common.Exceptions.NotFoundException
import com.kotlincrossplatform.nutrivox.common.PaginatedResponse
import com.kotlincrossplatform.nutrivox.common.PaginationParams
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.OffsetDateTime
import java.util.*

@Serializable
data class CreateAssessmentRequest(
    val assessmentType: String,
    val weightKg: Double? = null,
    val heightCm: Double? = null,
    val waistCm: Double? = null,
    val hipCm: Double? = null,
    val abdomenCm: Double? = null,
    val bodyFatPct: Double? = null,
    val muscleMassKg: Double? = null,
    val bodyWaterPct: Double? = null,
    val clinicalNotes: String? = null,
    val isDraft: Boolean? = null
)

@Serializable
data class AssessmentSummary(
    val id: String,
    val date: String,
    val weightKg: Double? = null,
    val bmi: Double? = null,
    val isDraft: Boolean
)

@Serializable
data class AssessmentDetail(
    val id: String,
    val patientId: String,
    val nutritionistId: String,
    val date: String,
    val assessmentType: String,
    val weightKg: Double? = null,
    val heightCm: Double? = null,
    val bmi: Double? = null,
    val waistCm: Double? = null,
    val hipCm: Double? = null,
    val abdomenCm: Double? = null,
    val bodyFatPct: Double? = null,
    val muscleMassKg: Double? = null,
    val bodyWaterPct: Double? = null,
    val clinicalNotes: String? = null,
    val isDraft: Boolean
)

@Serializable
data class AssessmentComparison(
    val assessment1: AssessmentDetail,
    val assessment2: AssessmentDetail,
    val diff: Map<String, Double>
)

class AssessmentService {

    fun createAssessment(patientId: UUID, nutritionistId: UUID, request: CreateAssessmentRequest): UUID = transaction {
        val assessmentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val now = OffsetDateTime.now()

        val bmi = calculateBmi(request.weightKg, request.heightCm)

        val id = UUID.randomUUID()
        AssessmentTable.insert {
            it[AssessmentTable.id] = id
            it[AssessmentTable.patientId] = patientId
            it[AssessmentTable.nutritionistId] = nutritionistId
            it[date] = assessmentDate
            it[assessmentType] = request.assessmentType
            it[weightKg] = request.weightKg?.toBigDecimal()
            it[heightCm] = request.heightCm?.toBigDecimal()
            it[AssessmentTable.bmi] = bmi?.toBigDecimal()
            it[waistCm] = request.waistCm?.toBigDecimal()
            it[hipCm] = request.hipCm?.toBigDecimal()
            it[abdomenCm] = request.abdomenCm?.toBigDecimal()
            it[bodyFatPct] = request.bodyFatPct?.toBigDecimal()
            it[muscleMassKg] = request.muscleMassKg?.toBigDecimal()
            it[bodyWaterPct] = request.bodyWaterPct?.toBigDecimal()
            it[clinicalNotes] = request.clinicalNotes
            it[isDraft] = request.isDraft ?: false
            it[createdAt] = now
            it[updatedAt] = now
        }
        id
    }

    fun listAssessments(patientId: UUID, pagination: PaginationParams): PaginatedResponse<AssessmentSummary> = transaction {
        val baseQuery = AssessmentTable.selectAll()
            .where { AssessmentTable.patientId eq patientId }

        val total = baseQuery.count()
        val items = baseQuery
            .orderBy(AssessmentTable.date, SortOrder.DESC)
            .limit(pagination.pageSize)
            .offset(pagination.offset.toLong())
            .map { row ->
                AssessmentSummary(
                    id = row[AssessmentTable.id].toString(),
                    date = row[AssessmentTable.date].toString(),
                    weightKg = row[AssessmentTable.weightKg]?.toDouble(),
                    bmi = row[AssessmentTable.bmi]?.toDouble(),
                    isDraft = row[AssessmentTable.isDraft]
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

    fun getAssessment(assessmentId: UUID): AssessmentDetail = transaction {
        val row = AssessmentTable.selectAll()
            .where { AssessmentTable.id eq assessmentId }
            .singleOrNull() ?: throw NotFoundException("Assessment not found")

        row.toAssessmentDetail()
    }

    fun compareAssessments(assessmentId1: UUID, assessmentId2: UUID): AssessmentComparison = transaction {
        val a1 = getAssessment(assessmentId1)
        val a2 = getAssessment(assessmentId2)

        val diff = mutableMapOf<String, Double>()

        computeDelta("weightKg", a1.weightKg, a2.weightKg)?.let { diff["weightKg"] = it }
        computeDelta("heightCm", a1.heightCm, a2.heightCm)?.let { diff["heightCm"] = it }
        computeDelta("bmi", a1.bmi, a2.bmi)?.let { diff["bmi"] = it }
        computeDelta("waistCm", a1.waistCm, a2.waistCm)?.let { diff["waistCm"] = it }
        computeDelta("hipCm", a1.hipCm, a2.hipCm)?.let { diff["hipCm"] = it }
        computeDelta("abdomenCm", a1.abdomenCm, a2.abdomenCm)?.let { diff["abdomenCm"] = it }
        computeDelta("bodyFatPct", a1.bodyFatPct, a2.bodyFatPct)?.let { diff["bodyFatPct"] = it }
        computeDelta("muscleMassKg", a1.muscleMassKg, a2.muscleMassKg)?.let { diff["muscleMassKg"] = it }
        computeDelta("bodyWaterPct", a1.bodyWaterPct, a2.bodyWaterPct)?.let { diff["bodyWaterPct"] = it }

        AssessmentComparison(
            assessment1 = a1,
            assessment2 = a2,
            diff = diff
        )
    }

    private fun calculateBmi(weightKg: Double?, heightCm: Double?): Double? {
        if (weightKg == null || heightCm == null || heightCm <= 0.0) return null
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)
        return BigDecimal(bmi).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    private fun computeDelta(field: String, v1: Double?, v2: Double?): Double? {
        if (v1 == null || v2 == null) return null
        val delta = v2 - v1
        return BigDecimal(delta).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    private fun ResultRow.toAssessmentDetail() = AssessmentDetail(
        id = this[AssessmentTable.id].toString(),
        patientId = this[AssessmentTable.patientId].toString(),
        nutritionistId = this[AssessmentTable.nutritionistId].toString(),
        date = this[AssessmentTable.date].toString(),
        assessmentType = this[AssessmentTable.assessmentType],
        weightKg = this[AssessmentTable.weightKg]?.toDouble(),
        heightCm = this[AssessmentTable.heightCm]?.toDouble(),
        bmi = this[AssessmentTable.bmi]?.toDouble(),
        waistCm = this[AssessmentTable.waistCm]?.toDouble(),
        hipCm = this[AssessmentTable.hipCm]?.toDouble(),
        abdomenCm = this[AssessmentTable.abdomenCm]?.toDouble(),
        bodyFatPct = this[AssessmentTable.bodyFatPct]?.toDouble(),
        muscleMassKg = this[AssessmentTable.muscleMassKg]?.toDouble(),
        bodyWaterPct = this[AssessmentTable.bodyWaterPct]?.toDouble(),
        clinicalNotes = this[AssessmentTable.clinicalNotes],
        isDraft = this[AssessmentTable.isDraft]
    )
}
