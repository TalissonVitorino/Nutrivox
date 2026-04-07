package com.kotlincrossplatform.nutrivox.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    inviteCode: String,
    viewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }

    LaunchedEffect(inviteCode) {
        viewModel.inviteCode = inviteCode
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        // Logo
        Surface(
            modifier = Modifier.size(64.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "N",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Bem-vindo ao Nutrivox", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Seu nutricionista convidou você!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        // Step indicator
        LinearProgressIndicator(
            progress = { if (step == 1) 0.5f else 1.0f },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Passo $step de 2",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (step == 1) {
                    // Step 1: Credentials
                    Text("Crie sua conta", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it; viewModel.clearError() },
                        label = { Text("E-mail") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it; viewModel.clearError() },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.confirmPassword,
                        onValueChange = { viewModel.confirmPassword = it; viewModel.clearError() },
                        label = { Text("Confirmar senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (viewModel.error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            viewModel.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (viewModel.password != viewModel.confirmPassword) {
                                viewModel.error = "As senhas não coincidem"
                            } else if (viewModel.password.length < 8) {
                                viewModel.error = "Senha deve ter pelo menos 8 caracteres"
                            } else {
                                viewModel.clearError()
                                step = 2
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Continuar")
                    }
                } else {
                    // Step 2: Terms & consent
                    Text("Termos e Consentimento", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = viewModel.acceptedTerms,
                            onCheckedChange = { viewModel.acceptedTerms = it }
                        )
                        Text(
                            "Li e aceito os Termos de Uso e Política de Privacidade",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = viewModel.aiConsent,
                            onCheckedChange = { viewModel.aiConsent = it }
                        )
                        Column(modifier = Modifier.padding(start = 4.dp)) {
                            Text(
                                "Autorizo o uso dos meus dados pelo assistente de IA",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Para sugestões nutricionais personalizadas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (viewModel.error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            viewModel.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Voltar")
                        }
                        Button(
                            onClick = { viewModel.acceptInvite(onSuccess) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            enabled = !viewModel.isLoading && viewModel.acceptedTerms,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Criar conta")
                            }
                        }
                    }
                }
            }
        }
    }
}
