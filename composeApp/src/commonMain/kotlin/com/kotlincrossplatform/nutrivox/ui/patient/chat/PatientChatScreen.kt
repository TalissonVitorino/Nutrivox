package com.kotlincrossplatform.nutrivox.ui.patient.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.*
import com.kotlincrossplatform.nutrivox.ui.components.AIDisclaimer

data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val isAI: Boolean = false,
    val timestamp: String = ""
)

@Composable
fun PatientChatScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Nutricionista", "Assistente")
    var messageInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf(
        ChatMessage("1", "Olá! Como posso ajudar com sua alimentação?", isUser = false, isAI = selectedTab == 1)
    ))}

    Column(modifier = modifier.fillMaxSize()) {
        // Tab selector
        @Suppress("DEPRECATION")
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        if (index == 1) Text("\u2728 $title") else Text(title)
                    }
                )
            }
        }

        // AI Disclaimer (only on Assistente tab)
        if (selectedTab == 1) {
            AIDisclaimer(
                text = "Assistente de IA. Respostas informativas, não substituem seu nutricionista.",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Messages
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            state = rememberLazyListState(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message, isAssistantTab = selectedTab == 1)
            }
        }

        // Quick actions (Assistente tab)
        if (selectedTab == 1 && messages.size <= 1) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("O que posso comer no lanche?", "Alternativas ao arroz?", "Dicas de hidratação").forEach { suggestion ->
                    SuggestionChip(
                        onClick = { messageInput = suggestion },
                        label = { Text(suggestion, style = MaterialTheme.typography.labelSmall, maxLines = 1) }
                    )
                }
            }
        }

        // Input
        Surface(shadowElevation = 4.dp) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("Digite sua mensagem...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            messages = messages + ChatMessage(
                                id = messages.size.toString(),
                                content = messageInput,
                                isUser = true
                            )
                            messageInput = ""
                            // TODO: call AI service or send to nutritionist
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("\u27A4", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, isAssistantTab: Boolean) {
    val isUser = message.isUser
    val alignment = if (isUser) Arrangement.End else Arrangement.Start
    val bubbleColor = when {
        isUser -> MaterialTheme.colorScheme.primary
        isAssistantTab -> AIPurpleSurface
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        isUser -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser && isAssistantTab) {
                    Text("\u2728 Assistente", style = MaterialTheme.typography.labelSmall, color = AIPurple)
                    Spacer(Modifier.height(4.dp))
                }
                Text(message.content, style = MaterialTheme.typography.bodyMedium, color = textColor)
            }
        }
    }
}
