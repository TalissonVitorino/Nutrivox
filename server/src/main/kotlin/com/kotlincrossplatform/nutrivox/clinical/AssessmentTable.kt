package com.kotlincrossplatform.nutrivox.clinical

import com.kotlincrossplatform.nutrivox.patients.PatientTable
import com.kotlincrossplatform.nutrivox.users.UserTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object AssessmentTable : Table("assessments") {
    val id = uuid("id").autoGenerate()
    val patientId = uuid("patient_id").references(PatientTable.id)
    val nutritionistId = uuid("nutritionist_id").references(UserTable.id)
    val date = date("date")
    val assessmentType = varchar("assessment_type", 50).default("in-person")
    val weightKg = decimal("weight_kg", 6, 2).nullable()
    val heightCm = decimal("height_cm", 6, 2).nullable()
    val bmi = decimal("bmi", 5, 2).nullable()
    val waistCm = decimal("waist_cm", 6, 2).nullable()
    val hipCm = decimal("hip_cm", 6, 2).nullable()
    val abdomenCm = decimal("abdomen_cm", 6, 2).nullable()
    val bodyFatPct = decimal("body_fat_pct", 5, 2).nullable()
    val muscleMassKg = decimal("muscle_mass_kg", 6, 2).nullable()
    val bodyWaterPct = decimal("body_water_pct", 5, 2).nullable()
    val clinicalNotes = text("clinical_notes").nullable()
    val isDraft = bool("is_draft").default(false)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    override val primaryKey = PrimaryKey(id)
}
