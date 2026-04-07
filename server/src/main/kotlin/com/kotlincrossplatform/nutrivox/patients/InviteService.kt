package com.kotlincrossplatform.nutrivox.patients

import com.kotlincrossplatform.nutrivox.auth.PasswordHasher
import com.kotlincrossplatform.nutrivox.common.Exceptions.ConflictException
import com.kotlincrossplatform.nutrivox.common.Exceptions.NotFoundException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ValidationException
import com.kotlincrossplatform.nutrivox.users.UserTable
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

data class CreateInviteRequest(
    val patientName: String,
    val patientEmail: String?,
    val patientPhone: String?,
    val patientSex: String,
    val patientDateOfBirth: LocalDate,
    val patientGoal: String?,
    val patientRestrictions: String?,
    val patientNotes: String?
)

data class AcceptInviteRequest(
    val inviteCode: String,
    val email: String,
    val password: String
)

class InviteService {

    fun createInvite(nutritionistId: UUID, request: CreateInviteRequest): String = transaction {
        // Verify the user is actually a nutritionist
        val user = UserTable.selectAll().where { UserTable.id eq nutritionistId }.singleOrNull()
            ?: throw NotFoundException("User not found")
        if (user[UserTable.role] != "nutritionist") {
            throw ValidationException("Only nutritionists can invite patients")
        }

        val code = UUID.randomUUID().toString().take(8).uppercase()
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val expiresAt = now.plusDays(7)

        PatientInviteTable.insert {
            it[PatientInviteTable.nutritionistId] = nutritionistId
            it[inviteCode] = code
            it[patientName] = request.patientName
            it[patientEmail] = request.patientEmail
            it[patientPhone] = request.patientPhone
            it[patientSex] = request.patientSex
            it[patientDateOfBirth] = request.patientDateOfBirth
            it[patientGoal] = request.patientGoal
            it[patientRestrictions] = request.patientRestrictions
            it[patientNotes] = request.patientNotes
            it[isUsed] = false
            it[PatientInviteTable.expiresAt] = expiresAt
            it[createdAt] = now
        }

        code
    }

    fun acceptInvite(request: AcceptInviteRequest): UUID = transaction {
        val invite = PatientInviteTable.selectAll()
            .where { PatientInviteTable.inviteCode eq request.inviteCode }
            .singleOrNull() ?: throw NotFoundException("Invalid invite code")

        if (invite[PatientInviteTable.isUsed]) {
            throw ValidationException("Invite already used")
        }
        if (invite[PatientInviteTable.expiresAt].toInstant()
                .isBefore(OffsetDateTime.now(ZoneOffset.UTC).toInstant())
        ) {
            throw ValidationException("Invite expired")
        }

        // Check email not already registered
        val existing = UserTable.selectAll()
            .where { UserTable.email eq request.email }
            .singleOrNull()
        if (existing != null) throw ConflictException("Email already registered")

        if (request.password.length < 8) {
            throw ValidationException("Password must be at least 8 characters")
        }

        val now = OffsetDateTime.now(ZoneOffset.UTC)

        // Create user
        val userId = UserTable.insert {
            it[email] = request.email
            it[passwordHash] = PasswordHasher.hash(request.password)
            it[role] = "patient"
            it[fullName] = invite[PatientInviteTable.patientName]
            it[phone] = invite[PatientInviteTable.patientPhone]
            it[isActive] = true
            it[createdAt] = now
            it[updatedAt] = now
        }[UserTable.id]

        // Create patient record
        val patientId = PatientTable.insert {
            it[PatientTable.userId] = userId
            it[sex] = invite[PatientInviteTable.patientSex]
            it[dateOfBirth] = invite[PatientInviteTable.patientDateOfBirth]
            it[primaryGoal] = invite[PatientInviteTable.patientGoal]
            it[dietaryRestrictions] = invite[PatientInviteTable.patientRestrictions]
            it[clinicalNotes] = invite[PatientInviteTable.patientNotes]
            it[aiConsent] = false
            it[createdAt] = now
            it[updatedAt] = now
        }[PatientTable.id]

        // Link nutritionist to patient
        NutritionistPatientLinkTable.insert {
            it[nutritionistId] = invite[PatientInviteTable.nutritionistId]
            it[NutritionistPatientLinkTable.patientId] = patientId
            it[isActive] = true
            it[createdAt] = now
        }

        // Mark invite as used
        PatientInviteTable.update({ PatientInviteTable.id eq invite[PatientInviteTable.id] }) {
            it[isUsed] = true
        }

        userId
    }
}
