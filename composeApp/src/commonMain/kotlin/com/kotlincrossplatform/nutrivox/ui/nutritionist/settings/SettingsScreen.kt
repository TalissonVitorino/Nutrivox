package com.kotlincrossplatform.nutrivox.ui.nutritionist.settings

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
fun SettingsScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var aiSuggestionsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Configuracoes", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        // Profile
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("RC", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Dr. Robert Chen", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Nutricionista Clinico",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                SettingField("Nome Completo", "Dr. Robert Chen")
                SettingField("Endereco de E-mail", "robert.chen@nutrivox.com")
                SettingField("Registro Profissional (CRN)", "CRN-3 45892")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Clinic
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Detalhes da Clinica", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                SettingField("Nome da Clinica", "Vitality Nutrition Center")
                SettingField("Numero de Contato", "+1 (555) 123-4567")
            }
        }

        Spacer(Modifier.height(16.dp))

        // AI Preferences
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AIPurpleSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Preferencias do Assistente de IA",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(shape = MaterialTheme.shapes.small, color = AIPurple) {
                        Text(
                            "Powered by AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Ativar Sugestoes Inteligentes",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "A IA sugerira alternativas de refeicoes e respostas do chat com base no historico do paciente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = aiSuggestionsEnabled,
                        onCheckedChange = { aiSuggestionsEnabled = it }
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Texto de Divulgacao para o Paciente",
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = "Algumas sugestoes neste plano sao geradas por IA e revisadas pelo seu nutricionista.",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall,
                    minLines = 2
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Security
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Privacidade e Seguranca", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                ToggleRow(
                    "Login Biometrico",
                    "Usar Face ID / Touch ID para abrir o aplicativo",
                    biometricEnabled
                ) { biometricEnabled = it }
                TextButton(onClick = {}) { Text("Tempo Limite da Sessao") }
            }
        }

        Spacer(Modifier.height(16.dp))

        // App preferences
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Preferencias do Aplicativo", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Notificacoes")
                }
                ToggleRow("Modo Escuro", "", darkMode) { darkMode = it }
                TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Ajuda e Suporte")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        TextButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = Error)
        ) {
            Text("Sair")
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Nutrivox App v2.4.1",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SettingField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            if (subtitle.isNotBlank()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}
