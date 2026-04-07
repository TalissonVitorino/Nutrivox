package com.kotlincrossplatform.nutrivox.ui.nutritionist.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.*
import com.kotlincrossplatform.nutrivox.ui.components.AIBadge

@Composable
fun DashboardScreen(
    onPatientClick: (patientId: String) -> Unit = {},
    onViewAllPatients: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Painel Principal", style = MaterialTheme.typography.headlineMedium)
        Text("Bem-vindo, Dr. Silva", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(20.dp))

        // Summary cards
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Consultas Hoje", "8", "+2", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            SummaryCard("Requerem Atenção", "12", "", Warning, Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Critical alerts
        Text("Alertas Críticos", style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = onViewAllPatients) { Text("Ver Todos") }

        Spacer(Modifier.height(8.dp))

        AlertCard("Sarah Jenkins", "Alto Risco", "Faltou a 3 retornos consecutivos", onMessage = {}, onReview = { onPatientClick("1") })
        Spacer(Modifier.height(8.dp))
        AlertCard("Marcus Chen", "Atenção", "Ingestão calórica 20% abaixo da meta", onMessage = {}, onReview = { onPatientClick("2") })

        Spacer(Modifier.height(20.dp))

        // AI Insights
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Insights de IA", style = MaterialTheme.typography.titleLarge)
            AIBadge(label = "Sugestão IA")
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AIPurpleSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ajuste de Macros Necessário", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "O sistema detectou uma tendência de baixa adesão de proteínas nas refeições matinais para pacientes no protocolo Preparo Atlético.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text("⚠ As sugestões de IA são baseadas em dados agregados e devem ser verificadas clinicamente.", style = MaterialTheme.typography.labelSmall, color = AIPurple)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {}) { Text("Rejeitar") }
                    Button(onClick = {}) { Text("Revisar Pacientes") }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Aggregated adherence
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Adesão Agregada", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    Text("Gráfico de linha — implementar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(12.dp))
                MetricRow("Calorias", "92%", "+2%")
                MetricRow("Proteínas", "88%", "-4%")
                MetricRow("Hidratação", "95%", "+5%")
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun SummaryCard(title: String, value: String, trend: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineLarge, color = color)
                if (trend.isNotBlank()) {
                    Spacer(Modifier.width(4.dp))
                    Text(trend, style = MaterialTheme.typography.labelSmall, color = Success)
                }
            }
        }
    }
}

@Composable
private fun AlertCard(name: String, severity: String, description: String, onMessage: () -> Unit, onReview: () -> Unit) {
    val severityColor = if (severity == "Alto Risco") Error else Warning
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Surface(shape = MaterialTheme.shapes.small, color = severityColor.copy(alpha = 0.15f)) {
                    Text(severity, style = MaterialTheme.typography.labelSmall, color = severityColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onMessage) { Text("Mensagem") }
                Button(onClick = onReview) { Text("Revisar Plano") }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, trend: String) {
    val trendColor = if (trend.startsWith("+")) Success else Error
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
        Text(trend, style = MaterialTheme.typography.labelMedium, color = trendColor)
    }
}
