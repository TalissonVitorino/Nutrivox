package com.kotlincrossplatform.nutrivox.consumption

import com.kotlincrossplatform.nutrivox.foods.FoodTable
import com.kotlincrossplatform.nutrivox.patients.PatientTable
import com.kotlincrossplatform.nutrivox.plans.DietVariationTable
import com.kotlincrossplatform.nutrivox.plans.MealItemTable
import com.kotlincrossplatform.nutrivox.plans.MealTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object ConsumptionRecordTable : Table("consumption_records") {
    val id = uuid("id").autoGenerate()
    val patientId = uuid("patient_id").references(PatientTable.id)
    val mealId = uuid("meal_id").references(MealTable.id).nullable()
    val variationId = uuid("variation_id").references(DietVariationTable.id).nullable()
    val date = date("date")
    val time = varchar("time", 8).nullable()
    val mode = varchar("mode", 30)
    val notes = text("notes").nullable()
    val photoUrl = text("photo_url").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object ConsumptionItemTable : Table("consumption_items") {
    val id = uuid("id").autoGenerate()
    val recordId = uuid("record_id").references(ConsumptionRecordTable.id)
    val mealItemId = uuid("meal_item_id").references(MealItemTable.id).nullable()
    val foodId = uuid("food_id").references(FoodTable.id).nullable()
    val foodName = varchar("food_name", 500)
    val quantityGrams = decimal("quantity_grams", 8, 2).nullable()
    val householdMeasure = varchar("household_measure", 100).nullable()
    val wasConsumed = bool("was_consumed").default(true)
    val isSubstitution = bool("is_substitution").default(false)
    val substitutionOrigin = varchar("substitution_origin", 30).nullable()
    val originalFoodName = varchar("original_food_name", 500).nullable()
    val isOffPlan = bool("is_off_plan").default(false)
    val calories = decimal("calories", 8, 2).nullable()
    val proteinG = decimal("protein_g", 8, 2).nullable()
    val carbsG = decimal("carbs_g", 8, 2).nullable()
    val fatG = decimal("fat_g", 8, 2).nullable()
    val fiberG = decimal("fiber_g", 8, 2).nullable()
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}
