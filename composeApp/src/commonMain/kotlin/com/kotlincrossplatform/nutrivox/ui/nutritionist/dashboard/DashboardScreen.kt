package com.kotlincrossplatform.nutrivox.ui.nutritionist.dashboard

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

data class PatientNote(
    val patientId: String,
    val patientName: String,
    val text: String,
    val timestamp: String
)

@Composable
fun DashboardScreen(
    onPatientClick: (patientId: String) -> Unit = {},
    onViewAllPatients: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // State for notes
    var notes by remember { mutableStateOf(listOf<PatientNote>()) }
    var showNotesDialog by remember { mutableStateOf(false) }
    var notesDialogPatientId by remember { mutableStateOf("") }
    var notesDialogPatientName by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }

    fun openNotes(patientId: String, patientName: String) {
        notesDialogPatientId = patientId
        notesDialogPatientName = patientName
        noteInput = ""
        showNotesDialog = true
    }

    // Notes dialog
    if (showNotesDialog) {
        val existingNotes = notes.filter { it.patientId == notesDialogPatientId }

        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = {
                Text("Observações — $notesDialogPatientName", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Column {
                    // Existing notes
                    if (existingNotes.isNotEmpty()) {
                        existingNotes.forEach { note ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(note.text, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        note.timestamp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                    }

                    Text(
                        "Nova observação",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        placeholder = { Text("Anotação clínica, orientação, lembrete...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteInput.isNotBlank()) {
                            notes = notes + PatientNote(
                                patientId = notesDialogPatientId,
                                patientName = notesDialogPatientName,
                                text = noteInput,
                                timestamp = "Agora"
                            )
                            noteInput = ""
                            showNotesDialog = false
                        }
                    },
                    modifier = Modifier.height(48.dp),
                    enabled = noteInput.isNotBlank()
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showNotesDialog = false },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Fechar")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Bem-vindo, Dr. Silva",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // Summary cards
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Consultas Hoje", "8", "+2", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            SummaryCard("Requerem Atenção", "12", "", Warning, Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Critical alerts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Alertas Críticos", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onViewAllPatients) { Text("Ver Todos") }
        }

        Spacer(Modifier.height(8.dp))

        AlertCard(
            name = "Sarah Jenkins",
            severity = "Alto Risco",
            description = "Faltou a 3 retornos consecutivos",
            noteCount = notes.count { it.patientId == "1" },
            onNotes = { openNotes("1", "Sarah Jenkins") },
            onReview = { onPatientClick("1") }
        )
        Spacer(Modifier.height(12.dp))
        AlertCard(
            name = "Marcus Chen",
            severity = "Atenção",
            description = "Ingestão calórica 20% abaixo da meta",
            noteCount = notes.count { it.patientId == "2" },
            onNotes = { openNotes("2", "Marcus Chen") },
            onReview = { onPatientClick("2") }
        )

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
                Text(
                    "⚠ As sugestões de IA são baseadas em dados agregados e devem ser verificadas clinicamente.",
                    style = MaterialTheme.typography.labelSmall,
                    color = AIPurple
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {}, modifier = Modifier.height(48.dp)) { Text("Rejeitar") }
                    Button(onClick = {}, modifier = Modifier.height(48.dp)) { Text("Revisar Pacientes") }
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
                    Text(
                        "Gráfico de linha — implementar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(12.dp))
                MetricRow("Calorias", "92%", "+2%")
                MetricRow("Proteínas", "88%", "-4%")
                MetricRow("Hidratação", "95%", "+5%")
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    trend: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
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
private fun AlertCard(
    name: String,
    severity: String,
    description: String,
    noteCount: Int = 0,
    onNotes: () -> Unit,
    onReview: () -> Unit
) {
    val severityColor = if (severity == "Alto Risco") Error else Warning
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Surface(shape = MaterialTheme.shapes.small, color = severityColor.copy(alpha = 0.15f)) {
                    Text(
                        severity,
                        style = MaterialTheme.typography.labelSmall,
                        color = severityColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onNotes, modifier = Modifier.height(48.dp)) {
                    val label = if (noteCount > 0) "Observações ($noteCount)" else "Observações"
                    Text(label)
                }
                Button(onClick = onReview, modifier = Modifier.height(48.dp)) {
                    Text("Revisar Plano")
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, trend: String) {
    val trendColor = if (trend.startsWith("+")) Success else Error
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
        Text(trend, style = MaterialTheme.typography.labelMedium, color = trendColor)
    }
}
