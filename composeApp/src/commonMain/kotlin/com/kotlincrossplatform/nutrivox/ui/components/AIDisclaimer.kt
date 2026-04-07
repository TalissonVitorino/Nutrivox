package com.kotlincrossplatform.nutrivox.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.AIPurple
import com.kotlincrossplatform.nutrivox.theme.AIPurpleSurface

@Composable
fun AIDisclaimer(
    text: String = "Gerado por IA. Não substitui seu nutricionista.",
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = AIPurpleSurface,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "\u2728 $text",
            style = MaterialTheme.typography.bodySmall,
            color = AIPurple,
            modifier = Modifier.padding(12.dp)
        )
    }
}
