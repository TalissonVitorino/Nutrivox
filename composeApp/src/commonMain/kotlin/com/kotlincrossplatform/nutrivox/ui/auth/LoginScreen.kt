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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (isNutritionist: Boolean) -> Unit,
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        // Logo placeholder
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "N",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Nutrivox",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Gestão Nutricional Clínica",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(40.dp))

        // Login card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Bem-vindo",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it; viewModel.clearError() },
                    label = { Text("E-mail") },
                    placeholder = { Text("seu@email.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it; viewModel.clearError() },
                    label = { Text("Senha") },
                    placeholder = { Text("Sua senha de acesso") },
                    visualTransformation = if (viewModel.isPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.isPasswordVisible = !viewModel.isPasswordVisible }) {
                            Text(if (viewModel.isPasswordVisible) "Ocultar" else "Mostrar",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onForgotPassword) {
                        Text(
                            "Esqueci minha senha",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (viewModel.error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = viewModel.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.login(onLoginSuccess) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !viewModel.isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Entrar", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onRegister) {
            Text("Nutricionista? Criar conta")
        }

        Spacer(Modifier.height(8.dp))

        // Footer
        Text(
            text = "Ao entrar, você concorda com nossos\nTermos de Uso e Política de Privacidade",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))
    }
}
