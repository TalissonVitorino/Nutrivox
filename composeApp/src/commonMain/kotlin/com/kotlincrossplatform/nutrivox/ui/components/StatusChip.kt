package com.kotlincrossplatform.nutrivox.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlincrossplatform.nutrivox.theme.*

@Composable
fun StatusChip(
    status: ConsumptionStatus,
    modifier: Modifier = Modifier
) {
    val (label, containerColor, labelColor) = when (status) {
        ConsumptionStatus.REGISTERED -> Triple("Registrado", SuccessLight, Success)
        ConsumptionStatus.PARTIAL -> Triple("Parcial", WarningLight, Warning)
        ConsumptionStatus.PENDING -> Triple("Pendente", NeutralLight, Neutral)
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
