package com.kotlincrossplatform.nutrivox.plans

import com.kotlincrossplatform.nutrivox.foods.FoodTable
import com.kotlincrossplatform.nutrivox.patients.PatientTable
import com.kotlincrossplatform.nutrivox.users.UserTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object MealPlanTable : Table("meal_plans") {
    val id = uuid("id").autoGenerate()
    val patientId = uuid("patient_id").references(PatientTable.id)
    val nutritionistId = uuid("nutritionist_id").references(UserTable.id)
    val name = varchar("name", 255)
    val objective = text("objective").nullable()
    val status = varchar("status", 20).default("draft")
    val startDate = date("start_date").nullable()
    val endDate = date("end_date").nullable()
    val generalNotes = text("general_notes").nullable()
    val goalCalories = decimal("goal_calories", 8, 2).nullable()
    val goalProteinG = decimal("goal_protein_g", 8, 2).nullable()
    val goalCarbsG = decimal("goal_carbs_g", 8, 2).nullable()
    val goalFatG = decimal("goal_fat_g", 8, 2).nullable()
    val goalFiberG = decimal("goal_fiber_g", 8, 2).nullable()
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object DietVariationTable : Table("diet_variations") {
    val id = uuid("id").autoGenerate()
    val planId = uuid("plan_id").references(MealPlanTable.id)
    val name = varchar("name", 255)
    val isDefault = bool("is_default").default(false)
    val isPatientAccessible = bool("is_patient_accessible").default(true)
    val sortOrder = integer("sort_order").default(0)
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}

object MealTable : Table("meals") {
    val id = uuid("id").autoGenerate()
    val variationId = uuid("variation_id").references(DietVariationTable.id)
    val name = varchar("name", 255)
    val suggestedTime = varchar("suggested_time", 8).nullable()
    val sortOrder = integer("sort_order").default(0)
    val notes = text("notes").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}

object MealItemTable : Table("meal_items") {
    val id = uuid("id").autoGenerate()
    val mealId = uuid("meal_id").references(MealTable.id)
    val foodId = uuid("food_id").references(FoodTable.id).nullable()
    val foodName = varchar("food_name", 500)
    val quantityGrams = decimal("quantity_grams", 8, 2).nullable()
    val householdMeasure = varchar("household_measure", 100).nullable()
    val isAdLibitum = bool("is_ad_libitum").default(false)
    val notes = text("notes").nullable()
    val calories = decimal("calories", 8, 2).nullable()
    val proteinG = decimal("protein_g", 8, 2).nullable()
    val carbsG = decimal("carbs_g", 8, 2).nullable()
    val fatG = decimal("fat_g", 8, 2).nullable()
    val fiberG = decimal("fiber_g", 8, 2).nullable()
    val sortOrder = integer("sort_order").default(0)
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}

object AuthorizedSubstitutionTable : Table("authorized_substitutions") {
    val id = uuid("id").autoGenerate()
    val mealItemId = uuid("meal_item_id").references(MealItemTable.id)
    val foodId = uuid("food_id").references(FoodTable.id).nullable()
    val foodName = varchar("food_name", 500)
    val quantityGrams = decimal("quantity_grams", 8, 2).nullable()
    val householdMeasure = varchar("household_measure", 100).nullable()
    val calories = decimal("calories", 8, 2).nullable()
    val proteinG = decimal("protein_g", 8, 2).nullable()
    val carbsG = decimal("carbs_g", 8, 2).nullable()
    val fatG = decimal("fat_g", 8, 2).nullable()
    val notes = text("notes").nullable()
    override val primaryKey = PrimaryKey(id)
}
