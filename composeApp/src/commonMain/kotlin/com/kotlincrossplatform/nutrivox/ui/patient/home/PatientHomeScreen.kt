package com.kotlincrossplatform.nutrivox.ui.patient.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.data.remote.TokenStorage
import com.kotlincrossplatform.nutrivox.theme.*
import com.kotlincrossplatform.nutrivox.ui.components.*

@Composable
fun PatientHomeScreen(
    viewModel: PatientHomeViewModel,
    onMealClick: (mealId: String) -> Unit = {},
    onRegisterConsumption: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { viewModel.loadData() }

    when {
        viewModel.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        viewModel.error != null && viewModel.plan == null -> {
            ErrorState(
                message = viewModel.error ?: "Erro ao carregar plano",
                onRetry = { viewModel.loadData() }
            )
        }
        viewModel.plan == null -> {
            EmptyState(
                title = "Sem plano ativo",
                description = "Seu nutricionista esta preparando seu plano alimentar. Voce sera notificado quando estiver pronto."
            )
        }
        else -> {
            val plan = viewModel.plan!!
            val variation = viewModel.selectedVariation
            val totals = viewModel.dailyTotals

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Greeting
                Text(
                    text = "Bom dia,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = TokenStorage.userId?.take(12) ?: "Paciente",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(16.dp))

                // Active plan banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Plano Ativo",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Text(
                            plan.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        if (plan.startDate != null) {
                            val dateRange = buildString {
                                append(plan.startDate)
                                if (plan.endDate != null) append(" a ${plan.endDate}")
                            }
                            Text(
                                dateRange,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Calorie progress
                if (plan.goalCalories != null) {
                    NutrientProgressBar(
                        consumed = totals?.totalCalories ?: 0.0,
                        goal = plan.goalCalories,
                        label = "Calorias",
                        unit = "kcal"
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Macro circles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MacroCircle(
                        label = "Proteina",
                        consumed = totals?.totalProtein ?: 0.0,
                        goal = plan.goalProteinG ?: 0.0,
                        color = ProteinColor
                    )
                    MacroCircle(
                        label = "Carbo",
                        consumed = totals?.totalCarbs ?: 0.0,
                        goal = plan.goalCarbsG ?: 0.0,
                        color = CarbColor
                    )
                    MacroCircle(
                        label = "Gordura",
                        consumed = totals?.totalFat ?: 0.0,
                        goal = plan.goalFatG ?: 0.0,
                        color = FatColor
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Variation selector (if multiple)
                if (plan.variations.size > 1) {
                    VariationSelector(
                        variations = plan.variations.filter { it.isPatientAccessible }.map {
                            VariationOption(it.id, it.name, it.isDefault)
                        },
                        selectedId = viewModel.selectedVariationId ?: "",
                        onSelect = { viewModel.selectVariation(it) }
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Meals header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Refeicoes de hoje", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Ver todas",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Meal cards
                if (variation != null) {
                    variation.meals.sortedBy { it.sortOrder }.forEach { meal ->
                        MealCard(
                            name = meal.name,
                            time = meal.suggestedTime,
                            calories = meal.totalCalories,
                            proteinG = meal.totalProtein,
                            carbsG = meal.totalCarbs,
                            fatG = meal.totalFat,
                            status = ConsumptionStatus.PENDING,
                            onClick = { onMealClick(meal.id) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // General notes
                if (!plan.generalNotes.isNullOrBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Orientacoes do nutricionista",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                plan.generalNotes,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

            }
        }
    }
}
