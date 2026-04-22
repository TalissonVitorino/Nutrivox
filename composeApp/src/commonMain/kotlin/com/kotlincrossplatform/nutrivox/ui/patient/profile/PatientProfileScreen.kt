package com.kotlincrossplatform.nutrivox.ui.patient.profile

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
fun PatientProfileScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var biometricsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Avatar placeholder
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("\uD83D\uDC64", style = MaterialTheme.typography.headlineLarge)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Carlos Eduardo", style = MaterialTheme.typography.titleLarge) // TODO: real data
        Text("carlos.eduardo@email.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("(11) 98765-4321", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = {}) { Text("Editar Dados") }

        Spacer(Modifier.height(24.dp))

        // Objectives
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Objetivos Atuais", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Hipertrofia", style = MaterialTheme.typography.titleSmall)
                        Text("Meta principal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(shape = MaterialTheme.shapes.small, color = SuccessLight) {
                        Text("Em andamento", style = MaterialTheme.typography.labelSmall, color = Success, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Peso Atual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("78.5 kg", style = MaterialTheme.typography.titleMedium)
                    }
                    Column {
                        Text("Meta", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("82.0 kg", style = MaterialTheme.typography.titleMedium)
                    }
                    Column {
                        Text("Progresso", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("45%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Preferences & restrictions
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Prefer\u00EAncias Alimentares", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("Restri\u00E7\u00F5es e Alergias", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    listOf("Lactose", "Amendoim").forEach { restriction ->
                        SuggestionChip(onClick = {}, label = { Text(restriction) })
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Prefer\u00EAncias", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    listOf("Frango", "Ovos", "Batata Doce").forEach { pref ->
                        SuggestionChip(onClick = {}, label = { Text(pref) })
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Settings
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Configura\u00E7\u00F5es", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                SettingsToggle("Lembretes de Refei\u00E7\u00E3o", notificationsEnabled) { notificationsEnabled = it }
                SettingsToggle("Tema Escuro", darkModeEnabled) { darkModeEnabled = it }
                SettingsToggle("Biometria / Face ID", biometricsEnabled) { biometricsEnabled = it }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Privacidade e Consentimentos")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Nutritionist card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Profissional e Suporte", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(40.dp), shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) { Text("\uD83D\uDC69\u200D\u2695") }
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Dra. Marina Silva", style = MaterialTheme.typography.titleSmall)
                        Text("CRN-3 12345", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Contato") }
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Agendar") }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        TextButton(onClick = onLogout, colors = ButtonDefaults.textButtonColors(contentColor = Error)) {
            Text("\u21AA Sair do Aplicativo")
        }

        Spacer(Modifier.height(16.dp))
        Text("Vers\u00E3o 2.4.1", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

    }
}

@Composable
private fun SettingsToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
