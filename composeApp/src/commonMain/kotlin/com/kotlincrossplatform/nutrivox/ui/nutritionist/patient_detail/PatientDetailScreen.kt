package com.kotlincrossplatform.nutrivox.ui.nutritionist.patient_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.*
import com.kotlincrossplatform.nutrivox.ui.components.AIBadge

@Composable
fun PatientDetailScreen(
    patientId: String,
    onEditPlan: () -> Unit = {},
    onNewAssessment: () -> Unit = {},
    onChat: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Prontuário", "Avaliações", "Planos", "Consumo")

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(48.dp), shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) { Text("S", style = MaterialTheme.typography.titleLarge) }
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Sarah Jenkins", style = MaterialTheme.typography.titleLarge) // TODO: real data
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("32a", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Surface(shape = MaterialTheme.shapes.small, color = Error.copy(alpha = 0.15f)) {
                                Text("Alto Risco", style = MaterialTheme.typography.labelSmall, color = Error, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onChat, modifier = Modifier.height(48.dp)) { Text("Iniciar chat") }
                    OutlinedButton(onClick = onNewAssessment, modifier = Modifier.height(48.dp)) { Text("Nova aval.") }
                    Button(onClick = onEditPlan, modifier = Modifier.height(48.dp)) { Text("Editar plano") }
                }

                Spacer(Modifier.height(12.dp))

                ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 0.dp) {
                    tabs.forEachIndexed { index, title ->
                        Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                    }
                }
            }
        }

        // Tab content
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> ProntuarioTab()
                1 -> AvaliacoesTab()
                2 -> PlanosTab(onEditPlan = onEditPlan)
                3 -> ConsumoTab()
            }
        }
    }
}

@Composable
private fun ProntuarioTab() {
    // AI Insight
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AIPurpleSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Insights de IA", style = MaterialTheme.typography.titleMedium)
                AIBadge(label = "Beta")
            }
            Spacer(Modifier.height(8.dp))
            Text("Com base nos últimos 3 registros alimentares, a paciente está consistentemente consumindo menos proteína que a meta em ~25g/dia. Considere sugerir um shake de proteína no meio da tarde.", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text("⚠ Sugestões de IA são apenas para referência. Sempre verifique com julgamento clínico.", style = MaterialTheme.typography.labelSmall, color = AIPurple)
        }
    }

    Spacer(Modifier.height(16.dp))

    // Visão geral
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Visão Geral", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            InfoRow("Objetivo Principal", "Perda de Peso")
            InfoRow("Peso Meta", "65.0 kg")
            Spacer(Modifier.height(12.dp))
            Text("Condições de Saúde", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = {}, label = { Text("Alergia a Amendoim") })
                SuggestionChip(onClick = {}, label = { Text("Intolerância ao Glúten") })
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // Clinical notes
    Text("Notas Clínicas", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    ClinicalNoteCard("Consulta de Retorno", "12 Out, 2023", "Paciente relatou sentir-se com mais energia. A digestão melhorou após a redução de laticínios.")
    Spacer(Modifier.height(8.dp))
    ClinicalNoteCard("Avaliação Inicial", "10 Set, 2023", "Primeira consulta. Meta definida para perda de peso com foco no controle dos sintomas da SOP.")
}

@Composable
private fun AvaliacoesTab() {
    Text("Histórico de avaliações antropométricas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(12.dp))
    // Placeholder
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Última avaliação: 12 Out, 2023", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            InfoRow("Peso", "68.5 kg")
            InfoRow("IMC", "25.2")
            InfoRow("Cintura", "82 cm")
            InfoRow("% Gordura", "28.5%")
        }
    }
}

@Composable
private fun PlanosTab(onEditPlan: () -> Unit) {
    Text("Planos Alimentares", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(12.dp))
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Fase de Hipertrofia 1", style = MaterialTheme.typography.titleMedium)
                Surface(shape = MaterialTheme.shapes.small, color = SuccessLight) {
                    Text("PUBLICADO", style = MaterialTheme.typography.labelSmall, color = Success, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Text("Desde 01 de Out, 2023", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column { Text("Calorias", style = MaterialTheme.typography.labelSmall); Text("2,450", style = MaterialTheme.typography.titleSmall) }
                Column { Text("Proteína", style = MaterialTheme.typography.labelSmall); Text("160g", style = MaterialTheme.typography.titleSmall) }
                Column { Text("Refeições", style = MaterialTheme.typography.labelSmall); Text("5", style = MaterialTheme.typography.titleSmall) }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEditPlan, modifier = Modifier.height(48.dp)) { Text("Editar") }
                OutlinedButton(onClick = {}, modifier = Modifier.height(48.dp)) { Text("Visualizar") }
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text("+ Criar Novo Plano") }
}

@Composable
private fun ConsumoTab() {
    Text("Registros de consumo do paciente", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(12.dp))
    Text("Calendário com indicadores por dia — implementar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ClinicalNoteCard(title: String, date: String, text: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
