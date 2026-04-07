package com.kotlincrossplatform.nutrivox.patients

import com.kotlincrossplatform.nutrivox.common.Exceptions.ForbiddenException
import com.kotlincrossplatform.nutrivox.common.Exceptions.NotFoundException
import com.kotlincrossplatform.nutrivox.common.PaginatedResponse
import com.kotlincrossplatform.nutrivox.common.PaginationParams
import com.kotlincrossplatform.nutrivox.users.UserTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.LowerCase
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class PatientSummary(
    val id: String,
    val userId: String,
    val fullName: String,
    val sex: String,
    val dateOfBirth: String,
    val primaryGoal: String?,
    val isActive: Boolean
)

@Serializable
data class PatientDetail(
    val id: String,
    val userId: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val sex: String,
    val dateOfBirth: String,
    val primaryGoal: String?,
    val dietaryRestrictions: String?,
    val clinicalNotes: String?,
    val aiConsent: Boolean
)

class PatientService {

    fun listPatients(
        nutritionistId: UUID,
        pagination: PaginationParams,
        nameFilter: String? = null
    ): PaginatedResponse<PatientSummary> = transaction {
        val joinedTable = NutritionistPatientLinkTable
            .innerJoin(PatientTable)
            .join(UserTable, JoinType.INNER, PatientTable.userId, UserTable.id)

        val baseCondition = NutritionistPatientLinkTable.nutritionistId eq nutritionistId
        val condition = if (nameFilter != null) {
            baseCondition and (LowerCase(UserTable.fullName) like "%${nameFilter.lowercase()}%")
        } else {
            baseCondition
        }

        val baseQuery = joinedTable.selectAll().where { condition }

        val total = baseQuery.count()
        val items = baseQuery
            .orderBy(UserTable.fullName)
            .limit(pagination.pageSize)
            .offset(pagination.offset.toLong())
            .map { row ->
                PatientSummary(
                    id = row[PatientTable.id].toString(),
                    userId = row[PatientTable.userId].toString(),
                    fullName = row[UserTable.fullName],
                    sex = row[PatientTable.sex],
                    dateOfBirth = row[PatientTable.dateOfBirth].toString(),
                    primaryGoal = row[PatientTable.primaryGoal],
                    isActive = row[NutritionistPatientLinkTable.isActive]
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

    fun getPatientDetail(nutritionistId: UUID, patientId: UUID): PatientDetail = transaction {
        // Verify link exists
        NutritionistPatientLinkTable.selectAll()
            .where {
                (NutritionistPatientLinkTable.nutritionistId eq nutritionistId) and
                    (NutritionistPatientLinkTable.patientId eq patientId)
            }.singleOrNull() ?: throw ForbiddenException("Patient not linked to this nutritionist")

        val row = PatientTable
            .innerJoin(UserTable)
            .selectAll()
            .where { PatientTable.id eq patientId }
            .singleOrNull() ?: throw NotFoundException("Patient not found")

        PatientDetail(
            id = row[PatientTable.id].toString(),
            userId = row[PatientTable.userId].toString(),
            fullName = row[UserTable.fullName],
            email = row[UserTable.email],
            phone = row[UserTable.phone],
            sex = row[PatientTable.sex],
            dateOfBirth = row[PatientTable.dateOfBirth].toString(),
            primaryGoal = row[PatientTable.primaryGoal],
            dietaryRestrictions = row[PatientTable.dietaryRestrictions],
            clinicalNotes = row[PatientTable.clinicalNotes],
            aiConsent = row[PatientTable.aiConsent]
        )
    }
}
