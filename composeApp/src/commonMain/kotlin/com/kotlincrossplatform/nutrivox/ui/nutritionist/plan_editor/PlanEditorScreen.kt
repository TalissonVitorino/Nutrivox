package com.kotlincrossplatform.nutrivox.ui.nutritionist.plan_editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.*

@Composable
fun PlanEditorScreen(
    patientId: String,
    planId: String? = null, // null = new plan
    onPreview: () -> Unit = {},
    onPublish: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var planName by remember { mutableStateOf("") }
    var objective by remember { mutableStateOf("") }
    var goalCalories by remember { mutableStateOf("") }
    var goalProtein by remember { mutableStateOf("") }
    var goalCarbs by remember { mutableStateOf("") }
    var goalFat by remember { mutableStateOf("") }

    // Mock meal structure
    data class EditorMealItem(val name: String, val quantity: String, val unit: String, val kcal: Int)
    data class EditorMeal(val name: String, val time: String, val items: List<EditorMealItem>, val expanded: Boolean = true)

    var meals by remember {
        mutableStateOf(
            listOf(
                EditorMeal(
                    "Café da Manhã", "07:00 - 08:30", listOf(
                        EditorMealItem("Mingau de Aveia com Frutas", "60", "g", 230),
                        EditorMealItem("Ovos Cozidos", "2", "unid", 140)
                    )
                ),
                EditorMeal("Almoço", "12:30 - 14:00", emptyList()),
                EditorMeal(
                    "Lanche da Tarde", "16:00 - 16:30", listOf(
                        EditorMealItem("Whey Protein Isolado", "30", "g", 120)
                    )
                )
            )
        )
    }

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onPreview,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Visualizar")
                    }
                    Button(
                        onClick = onPublish,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Publicar Plano")
                    }
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
            Text("Editor de Plano", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            // Tags/chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = {}, label = { Text("Sem Gluten") })
                SuggestionChip(onClick = {}, label = { Text("Alto Teor de Proteina") })
                SuggestionChip(onClick = {}, label = { Text("+ Adicionar") })
            }

            Spacer(Modifier.height(16.dp))

            // Plan basics
            OutlinedTextField(
                value = planName,
                onValueChange = { planName = it },
                label = { Text("Nome do plano") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = objective,
                onValueChange = { objective = it },
                label = { Text("Objetivo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Nutritional goals
            Text("Metas Nutricionais", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = goalCalories,
                    onValueChange = { goalCalories = it },
                    label = { Text("Kcal") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = goalProtein,
                    onValueChange = { goalProtein = it },
                    label = { Text("Proteina (g)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = goalCarbs,
                    onValueChange = { goalCarbs = it },
                    label = { Text("Carbo (g)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = goalFat,
                    onValueChange = { goalFat = it },
                    label = { Text("Gordura (g)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(20.dp))

            // Meals
            meals.forEachIndexed { mealIndex, meal ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(meal.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${meal.time} - ${meal.items.sumOf { it.kcal }} kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (meal.items.isEmpty()) {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        "Adicionar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            } else {
                                TextButton(onClick = {
                                    meals = meals.toMutableList().apply {
                                        this[mealIndex] = meal.copy(expanded = !meal.expanded)
                                    }
                                }) {
                                    Text(if (meal.expanded) "Recolher" else "Expandir")
                                }
                            }
                        }

                        if (meal.expanded && meal.items.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            meal.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, style = MaterialTheme.typography.bodyMedium)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Surface(
                                                shape = MaterialTheme.shapes.extraSmall,
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    item.quantity,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                item.unit,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                "${item.kcal} kcal",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Buscar Ingrediente")
                            }
                        }
                    }
                }

                // AI suggestion between meals
                if (mealIndex == 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = AIPurpleSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "1 Sugestao de IA Disponivel",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = AIPurple
                                )
                                Text(
                                    "Otimizacao de macronutrientes do almoco",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                ">",
                                style = MaterialTheme.typography.titleLarge,
                                color = AIPurple
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Adicionar Refeicao")
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}
