package com.kotlincrossplatform.nutrivox.ui.patient.progress

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
fun ProgressScreen(
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("30 Dias") }
    val periods = listOf("7 Dias", "30 Dias", "90 Dias")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Evolucao e Metas",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            "Acompanhamento Clinico",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // Period selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            periods.forEach { period ->
                FilterChip(
                    selected = period == selectedPeriod,
                    onClick = { selectedPeriod = period },
                    label = { Text(period) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Adherence card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Adesao Semanal",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = SuccessLight
                    ) {
                        Text(
                            "Alcancado",
                            style = MaterialTheme.typography.labelSmall,
                            color = Success,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "92%",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Protein average
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Proteina Media",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = WarningLight
                    ) {
                        Text(
                            "Atencao",
                            style = MaterialTheme.typography.labelSmall,
                            color = Warning,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "1.6g/kg",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Hydration
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Hidratacao Media",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = SuccessLight
                    ) {
                        Text(
                            "Alcancado",
                            style = MaterialTheme.typography.labelSmall,
                            color = Success,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "2.8L",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    "/ 2.5L meta diaria",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Calorie chart placeholder
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Consumo Calorico",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(12.dp))
                // Placeholder for bar chart — to be replaced with a charting library
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Grafico de barras — implementar com biblioteca de charts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Nutritionist recommendation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Recomendacoes da Nutri",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "\"Excelente evolucao neste mes! Para os proximos 30 dias, vamos focar em aumentar a ingestao de fibras no jantar para melhorar a saciedade noturna.\"",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Bottom spacing for nav bar
        Spacer(Modifier.height(80.dp))
    }
}
