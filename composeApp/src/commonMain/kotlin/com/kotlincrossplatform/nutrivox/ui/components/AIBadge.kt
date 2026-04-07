package com.kotlincrossplatform.nutrivox.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.AIPurple
import com.kotlincrossplatform.nutrivox.theme.AIPurpleSurface

@Composable
fun AIBadge(
    label: String = "Sugestão de IA",
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = AIPurpleSurface,
        modifier = modifier
    ) {
        Text(
            text = "\u2728 $label",
            style = MaterialTheme.typography.labelSmall,
            color = AIPurple,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
