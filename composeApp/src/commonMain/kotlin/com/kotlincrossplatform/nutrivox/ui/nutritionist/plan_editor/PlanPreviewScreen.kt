package com.kotlincrossplatform.nutrivox.ui.nutritionist.plan_editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.*
import com.kotlincrossplatform.nutrivox.ui.components.MacroCircle

@Composable
fun PlanPreviewScreen(
    planId: String,
    onEdit: () -> Unit = {},
    onPublish: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Editar Plano")
                    }
                    Button(
                        onClick = onPublish,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Publicar para Paciente")
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Surface(
                modifier = Modifier.size(56.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("N", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            Text("Nutrivox", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(20.dp))

            // Macros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroCircle("Carbo", consumed = 210.0, goal = 210.0, color = CarbColor)
                MacroCircle("Proteina", consumed = 160.0, goal = 160.0, color = ProteinColor)
                MacroCircle("Gordura", consumed = 50.0, goal = 50.0, color = FatColor)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Refeicoes do Dia",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "4 Refeicoes Planejadas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Preview meal cards
            PreviewMealCard(
                "Cafe da Manha",
                "07:00 - 08:30",
                "450 kcal",
                listOf(
                    "Mingau de Aveia com Frutas" to "60g - 230 kcal",
                    "Ovos Cozidos" to "2 unidades - 140 kcal"
                )
            )
            Spacer(Modifier.height(8.dp))
            PreviewMealCard(
                "Almoco",
                "12:30 - 14:00",
                "420 kcal",
                listOf("Frango Grelhado com Quinoa" to "150g Frango, 100g Quinoa")
            )
            Spacer(Modifier.height(8.dp))
            PreviewMealCard(
                "Lanche da Tarde",
                "16:00 - 16:30",
                "200 kcal",
                listOf("Whey Protein Isolado" to "30g - 120 kcal")
            )

            Spacer(Modifier.height(20.dp))

            // Nutritionist notes
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Notas do Nutricionista", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Foco na hidratacao hoje, tente beber pelo menos 2.5L de agua. O almoco sugerido pela IA esta perfeitamente equilibrado para seus objetivos de hipertrofia.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Itens marcados com o selo de IA sao sugestoes inteligentes adaptadas aos seus objetivos. Voce pode troca-los se necessario.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PreviewMealCard(
    name: String,
    time: String,
    kcal: String,
    items: List<Pair<String, String>>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        kcal,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            items.forEach { (itemName, detail) ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        "- ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(itemName, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = {}) { Text("Ver Detalhes") }
        }
    }
}
