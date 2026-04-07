package com.kotlincrossplatform.nutrivox.ui.patient.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.data.repository.MealResponse
import com.kotlincrossplatform.nutrivox.theme.*

@Composable
fun MealDetailScreen(
    meal: MealResponse,
    onRegisterConsumption: (String) -> Unit = {},
    onAISuggestion: (itemId: String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { onRegisterConsumption(meal.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Registrar Consumo", style = MaterialTheme.typography.titleMedium)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(meal.name, style = MaterialTheme.typography.headlineMedium)
                    if (meal.suggestedTime != null) {
                        Text(
                            meal.suggestedTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Meal totals
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${meal.totalCalories.toInt()}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "kcal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${meal.totalProtein.toInt()}g",
                            style = MaterialTheme.typography.titleMedium,
                            color = ProteinColor
                        )
                        Text(
                            "Proteina",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${meal.totalCarbs.toInt()}g",
                            style = MaterialTheme.typography.titleMedium,
                            color = CarbColor
                        )
                        Text(
                            "Carbo",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${meal.totalFat.toInt()}g",
                            style = MaterialTheme.typography.titleMedium,
                            color = FatColor
                        )
                        Text(
                            "Gordura",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Items
            meal.items.sortedBy { it.foodName }.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                item.foodName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "${item.calories?.toInt() ?: "-"} kcal",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Measure (primary) + grams (secondary)
                        val measureText = buildString {
                            if (item.householdMeasure != null) append(item.householdMeasure)
                            if (item.quantityGrams != null) {
                                if (item.householdMeasure != null) append(" (${item.quantityGrams.toInt()}g)")
                                else append("${item.quantityGrams.toInt()}g")
                            }
                            if (item.isAdLibitum) append("A vontade")
                        }
                        if (measureText.isNotBlank()) {
                            Text(
                                measureText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (item.notes != null) {
                            Text(
                                item.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Substitutions
                        if (item.substitutions.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Substituicoes permitidas:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            item.substitutions.forEach { sub ->
                                val subText = buildString {
                                    append("- ${sub.foodName}")
                                    if (sub.householdMeasure != null) append(" -- ${sub.householdMeasure}")
                                }
                                Text(
                                    subText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Action chips
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (item.substitutions.isNotEmpty()) {
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            "Substituicoes",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                            AssistChip(
                                onClick = { onAISuggestion(item.id) },
                                label = {
                                    Text(
                                        "Sugestao IA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AIPurple
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Meal notes
            if (meal.notes != null) {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = meal.notes,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
