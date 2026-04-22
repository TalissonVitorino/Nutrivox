package com.kotlincrossplatform.nutrivox.ui.nutritionist.patients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.*

data class PatientListItem(
    val id: String, val name: String, val goal: String,
    val adherence: String, val adherenceLevel: String,
    val lastAnthropometry: String, val lastContact: String
)

@Composable
fun PatientListScreen(
    onPatientClick: (String) -> Unit = {},
    onNewPatient: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    // Mock data — TODO: load from API
    val patients = listOf(
        PatientListItem("1", "Sarah Jenkins", "Perda de Peso", "Baixa Adesão", "low", "12 Out, 2023", "Há 2 dias"),
        PatientListItem("2", "Marcus Chen", "Ganho de Massa", "Alta Adesão", "high", "05 Nov, 2023", "Hoje, 09:30"),
        PatientListItem("3", "Elena Rodriguez", "Manutenção", "Adesão Média", "medium", "28 Set, 2023", "Há 1 semana"),
        PatientListItem("4", "David Kim", "Prep. Esportiva", "Plano Inativo", "inactive", "15 Ago, 2023", "Há 1 mês")
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewPatient, containerColor = MaterialTheme.colorScheme.primary) {
                Text("+", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { paddingValues ->
        Column(modifier = modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("Gerencie seus ${patients.size} pacientes ativos", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar paciente...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(patients.filter { it.name.contains(searchQuery, ignoreCase = true) }) { patient ->
                    PatientCard(patient = patient, onClick = { onPatientClick(patient.id) })
                }
            }
        }
    }
}

@Composable
private fun PatientCard(patient: PatientListItem, onClick: () -> Unit) {
    val badgeColor = when (patient.adherenceLevel) {
        "high" -> Success
        "medium" -> Warning
        "low" -> Error
        else -> Neutral
    }

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(40.dp), shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) { Text(patient.name.take(1), style = MaterialTheme.typography.titleMedium) }
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(patient.name, style = MaterialTheme.typography.titleMedium)
                        Text("Meta: ${patient.goal}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Surface(shape = MaterialTheme.shapes.small, color = badgeColor.copy(alpha = 0.15f)) {
                    Text(patient.adherence, style = MaterialTheme.typography.labelSmall, color = badgeColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Última Antropometria", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(patient.lastAnthropometry, style = MaterialTheme.typography.bodySmall)
                }
                Column {
                    Text("Último Contato", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(patient.lastContact, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
