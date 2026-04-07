package com.kotlincrossplatform.nutrivox.patients

import com.kotlincrossplatform.nutrivox.users.UserTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object PatientTable : Table("patients") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UserTable.id).uniqueIndex()
    val sex = varchar("sex", 20)
    val dateOfBirth = date("date_of_birth")
    val primaryGoal = varchar("primary_goal", 255).nullable()
    val dietaryRestrictions = text("dietary_restrictions").nullable()
    val clinicalNotes = text("clinical_notes").nullable()
    val aiConsent = bool("ai_consent").default(false)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object NutritionistPatientLinkTable : Table("nutritionist_patient_links") {
    val id = uuid("id").autoGenerate()
    val nutritionistId = uuid("nutritionist_id").references(UserTable.id)
    val patientId = uuid("patient_id").references(PatientTable.id)
    val isActive = bool("is_active").default(true)
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(nutritionistId, patientId)
    }
}

object PatientInviteTable : Table("patient_invites") {
    val id = uuid("id").autoGenerate()
    val nutritionistId = uuid("nutritionist_id").references(UserTable.id)
    val inviteCode = varchar("invite_code", 100).uniqueIndex()
    val patientName = varchar("patient_name", 255)
    val patientEmail = varchar("patient_email", 255).nullable()
    val patientPhone = varchar("patient_phone", 50).nullable()
    val patientSex = varchar("patient_sex", 20)
    val patientDateOfBirth = date("patient_date_of_birth")
    val patientGoal = varchar("patient_goal", 255).nullable()
    val patientRestrictions = text("patient_restrictions").nullable()
    val patientNotes = text("patient_notes").nullable()
    val isUsed = bool("is_used").default(false)
    val expiresAt = timestampWithTimeZone("expires_at")
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}
