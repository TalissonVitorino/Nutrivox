package com.kotlincrossplatform.nutrivox.util

import com.kotlincrossplatform.nutrivox.model.NutrientValues

object NutritionCalculator {

    /** Calculate nutrients for a food item based on quantity in grams and per-100g values */
    fun calculateForQuantity(
        quantityGrams: Double,
        caloriesPer100g: Double?,
        proteinPer100g: Double?,
        carbsPer100g: Double?,
        fatPer100g: Double?,
        fiberPer100g: Double?
    ): NutrientValues {
        val factor = quantityGrams / 100.0
        return NutrientValues(
            calories = (caloriesPer100g ?: 0.0) * factor,
            proteinG = (proteinPer100g ?: 0.0) * factor,
            carbsG = (carbsPer100g ?: 0.0) * factor,
            fatG = (fatPer100g ?: 0.0) * factor,
            fiberG = (fiberPer100g ?: 0.0) * factor
        )
    }

    /** Sum nutrients from multiple items */
    fun sumNutrients(items: List<NutrientValues>): NutrientValues {
        return NutrientValues(
            calories = items.sumOf { it.calories },
            proteinG = items.sumOf { it.proteinG },
            carbsG = items.sumOf { it.carbsG },
            fatG = items.sumOf { it.fatG },
            fiberG = items.sumOf { it.fiberG }
        )
    }

    /** Calculate progress percentage (consumed vs goal), capped at 0-200% */
    fun progressPercentage(consumed: Double, goal: Double?): Double {
        if (goal == null || goal <= 0.0) return 0.0
        return ((consumed / goal) * 100.0).coerceIn(0.0, 200.0)
    }

    /** Calculate BMI from weight (kg) and height (cm) */
    fun calculateBMI(weightKg: Double, heightCm: Double): Double {
        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }

    /** Scale nutrients proportionally (e.g., patient ate 50% of portion) */
    fun scaleNutrients(nutrients: NutrientValues, factor: Double): NutrientValues {
        return NutrientValues(
            calories = nutrients.calories * factor,
            proteinG = nutrients.proteinG * factor,
            carbsG = nutrients.carbsG * factor,
            fatG = nutrients.fatG * factor,
            fiberG = nutrients.fiberG * factor
        )
    }
}
