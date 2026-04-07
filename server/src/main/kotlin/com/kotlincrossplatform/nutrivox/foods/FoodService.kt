package com.kotlincrossplatform.nutrivox.foods

import com.kotlincrossplatform.nutrivox.common.Exceptions.NotFoundException
import com.kotlincrossplatform.nutrivox.common.PaginatedResponse
import com.kotlincrossplatform.nutrivox.common.PaginationParams
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.LowerCase
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class FoodSummary(
    val id: String,
    val name: String,
    val category: String?,
    val caloriesPer100g: Double?,
    val proteinPer100g: Double?,
    val carbsPer100g: Double?,
    val fatPer100g: Double?
)

@Serializable
data class FoodDetail(
    val id: String,
    val name: String,
    val category: String?,
    val caloriesPer100g: Double?,
    val proteinPer100g: Double?,
    val carbsPer100g: Double?,
    val fatPer100g: Double?,
    val fiberPer100g: Double?,
    val sodiumPer100g: Double?,
    val source: String?,
    val householdMeasures: List<HouseholdMeasureDTO>
)

@Serializable
data class HouseholdMeasureDTO(
    val id: String,
    val name: String,
    val grams: Double
)

class FoodService {

    fun searchFoods(
        query: String,
        category: String?,
        pagination: PaginationParams
    ): PaginatedResponse<FoodSummary> = transaction {
        val baseCondition = FoodTable.isActive eq true
        val searchCondition = if (query.isNotBlank()) {
            baseCondition and (LowerCase(FoodTable.name) like "%${query.lowercase()}%")
        } else {
            baseCondition
        }
        val fullCondition = if (category != null) {
            searchCondition and (LowerCase(FoodTable.category) like "%${category.lowercase()}%")
        } else {
            searchCondition
        }

        val baseQuery = FoodTable.selectAll().where { fullCondition }

        val total = baseQuery.count()
        val items = baseQuery
            .orderBy(FoodTable.name)
            .limit(pagination.pageSize)
            .offset(pagination.offset.toLong())
            .map { row ->
                FoodSummary(
                    id = row[FoodTable.id].toString(),
                    name = row[FoodTable.name],
                    category = row[FoodTable.category],
                    caloriesPer100g = row[FoodTable.caloriesPer100g]?.toDouble(),
                    proteinPer100g = row[FoodTable.proteinPer100g]?.toDouble(),
                    carbsPer100g = row[FoodTable.carbsPer100g]?.toDouble(),
                    fatPer100g = row[FoodTable.fatPer100g]?.toDouble()
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

    fun getFoodDetail(foodId: UUID): FoodDetail = transaction {
        val row = FoodTable.selectAll()
            .where { (FoodTable.id eq foodId) and (FoodTable.isActive eq true) }
            .singleOrNull() ?: throw NotFoundException("Food not found")

        val measures = getFoodMeasuresInternal(foodId)

        FoodDetail(
            id = row[FoodTable.id].toString(),
            name = row[FoodTable.name],
            category = row[FoodTable.category],
            caloriesPer100g = row[FoodTable.caloriesPer100g]?.toDouble(),
            proteinPer100g = row[FoodTable.proteinPer100g]?.toDouble(),
            carbsPer100g = row[FoodTable.carbsPer100g]?.toDouble(),
            fatPer100g = row[FoodTable.fatPer100g]?.toDouble(),
            fiberPer100g = row[FoodTable.fiberPer100g]?.toDouble(),
            sodiumPer100g = row[FoodTable.sodiumPer100g]?.toDouble(),
            source = row[FoodTable.foodSource],
            householdMeasures = measures
        )
    }

    fun getFoodMeasures(foodId: UUID): List<HouseholdMeasureDTO> = transaction {
        // Verify food exists
        FoodTable.selectAll()
            .where { (FoodTable.id eq foodId) and (FoodTable.isActive eq true) }
            .singleOrNull() ?: throw NotFoundException("Food not found")

        getFoodMeasuresInternal(foodId)
    }

    private fun getFoodMeasuresInternal(foodId: UUID): List<HouseholdMeasureDTO> {
        return HouseholdMeasureTable.selectAll()
            .where { HouseholdMeasureTable.foodId eq foodId }
            .map { row ->
                HouseholdMeasureDTO(
                    id = row[HouseholdMeasureTable.id].toString(),
                    name = row[HouseholdMeasureTable.name],
                    grams = row[HouseholdMeasureTable.grams].toDouble()
                )
            }
    }
}
