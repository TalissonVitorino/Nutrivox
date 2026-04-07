package com.kotlincrossplatform.nutrivox.clinical

import com.kotlincrossplatform.nutrivox.patients.PatientTable
import com.kotlincrossplatform.nutrivox.users.UserTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object ClinicalRecordTable : Table("clinical_records") {
    val id = uuid("id").autoGenerate()
    val patientId = uuid("patient_id").references(PatientTable.id)
    val chiefComplaint = text("chief_complaint").nullable()
    val familyHistory = text("family_history").nullable()
    val pathologies = text("pathologies").nullable()
    val intolerances = text("intolerances").nullable()
    val allergies = text("allergies").nullable()
    val medications = text("medications").nullable()
    val supplementation = text("supplementation").nullable()
    val bowelHabits = text("bowel_habits").nullable()
    val sleepPattern = text("sleep_pattern").nullable()
    val physicalActivity = text("physical_activity").nullable()
    val waterIntake = text("water_intake").nullable()
    val foodPreferences = text("food_preferences").nullable()
    val foodAversions = text("food_aversions").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object ClinicalEvolutionTable : Table("clinical_evolutions") {
    val id = uuid("id").autoGenerate()
    val patientId = uuid("patient_id").references(PatientTable.id)
    val nutritionistId = uuid("nutritionist_id").references(UserTable.id)
    val date = date("date")
    val generalNotes = text("general_notes").nullable()
    val planAdherence = text("plan_adherence").nullable()
    val complications = text("complications").nullable()
    val reportedSymptoms = text("reported_symptoms").nullable()
    val adjustments = text("adjustments").nullable()
    val recommendations = text("recommendations").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}
