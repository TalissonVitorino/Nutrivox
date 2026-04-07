package com.kotlincrossplatform.nutrivox.foods

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object FoodTable : Table("foods") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 500)
    val category = varchar("category", 100).nullable()
    val caloriesPer100g = decimal("calories_per_100g", 8, 2).nullable()
    val proteinPer100g = decimal("protein_per_100g", 8, 2).nullable()
    val carbsPer100g = decimal("carbs_per_100g", 8, 2).nullable()
    val fatPer100g = decimal("fat_per_100g", 8, 2).nullable()
    val fiberPer100g = decimal("fiber_per_100g", 8, 2).nullable()
    val sodiumPer100g = decimal("sodium_per_100g", 8, 2).nullable()
    val calciumMg = decimal("calcium_mg", 8, 2).nullable()
    val ironMg = decimal("iron_mg", 8, 2).nullable()
    val magnesiumMg = decimal("magnesium_mg", 8, 2).nullable()
    val potassiumMg = decimal("potassium_mg", 8, 2).nullable()
    val zincMg = decimal("zinc_mg", 8, 2).nullable()
    val vitaminAMcg = decimal("vitamin_a_mcg", 8, 2).nullable()
    val vitaminCMg = decimal("vitamin_c_mg", 8, 2).nullable()
    val vitaminDMcg = decimal("vitamin_d_mcg", 8, 2).nullable()
    val foodSource = varchar("source", 100).nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}

object HouseholdMeasureTable : Table("household_measures") {
    val id = uuid("id").autoGenerate()
    val foodId = uuid("food_id").references(FoodTable.id)
    val name = varchar("name", 100)
    val grams = decimal("grams", 8, 2)
    override val primaryKey = PrimaryKey(id)
}
