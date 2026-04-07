package com.kotlincrossplatform.nutrivox.ui.patient.consumption

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.data.repository.MealResponse

data class ConsumptionItemState(
    val itemId: String,
    val foodName: String,
    val quantityGrams: Double?,
    val householdMeasure: String?,
    val calories: Double?,
    val isConsumed: Boolean = true,
    val adjustedQuantity: Double? = null
)

@Composable
fun ConsumptionScreen(
    meal: MealResponse,
    onSave: (items: List<ConsumptionItemState>, mode: String) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var itemStates by remember {
        mutableStateOf(
            meal.items.map { item ->
                ConsumptionItemState(
                    itemId = item.id,
                    foodName = item.foodName,
                    quantityGrams = item.quantityGrams,
                    householdMeasure = item.householdMeasure,
                    calories = item.calories,
                    isConsumed = true,
                    adjustedQuantity = item.quantityGrams
                )
            }
        )
    }

    val totalCalories = itemStates.filter { it.isConsumed }.sumOf { state ->
        val original = meal.items.find { it.id == state.itemId }
        if (original?.quantityGrams != null && original.quantityGrams > 0 && state.adjustedQuantity != null) {
            (original.calories ?: 0.0) * (state.adjustedQuantity / original.quantityGrams)
        } else {
            original?.calories ?: 0.0
        }
    }

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        val allConsumed = itemStates.all { it.isConsumed }
                        val mode = if (allConsumed) "full" else "partial"
                        onSave(itemStates, mode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Salvar Registro", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Text(
                "Registrar Consumo",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "${meal.name} - Hoje",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // Quick action — mark all consumed
            OutlinedButton(
                onClick = {
                    itemStates = itemStates.map {
                        it.copy(isConsumed = true, adjustedQuantity = it.quantityGrams)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Comi tudo")
            }

            Spacer(Modifier.height(16.dp))

            // Calorie summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${totalCalories.toInt()}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Food items with checkboxes and quantity adjusters
            itemStates.forEachIndexed { index, state ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.isConsumed,
                            onCheckedChange = { checked ->
                                itemStates = itemStates.toMutableList().apply {
                                    this[index] = state.copy(isConsumed = checked)
                                }
                            }
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                state.foodName,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                state.householdMeasure
                                    ?: "${state.quantityGrams?.toInt() ?: "-"}g",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Quantity adjuster (only when consumed and quantity is known)
                        if (state.isConsumed && state.quantityGrams != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val step = state.quantityGrams * 0.25
                                        val current = state.adjustedQuantity
                                            ?: state.quantityGrams
                                        val newQty = (current - step).coerceAtLeast(0.0)
                                        itemStates = itemStates.toMutableList().apply {
                                            this[index] = state.copy(
                                                adjustedQuantity = newQty
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("\u2212") // minus sign
                                }
                                Text(
                                    "${(state.adjustedQuantity ?: state.quantityGrams).toInt()}g",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.widthIn(min = 48.dp)
                                )
                                IconButton(
                                    onClick = {
                                        val step = state.quantityGrams * 0.25
                                        val current = state.adjustedQuantity
                                            ?: state.quantityGrams
                                        val newQty = current + step
                                        itemStates = itemStates.toMutableList().apply {
                                            this[index] = state.copy(
                                                adjustedQuantity = newQty
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("+")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
