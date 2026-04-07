package com.kotlincrossplatform.nutrivox.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class VariationOption(
    val id: String,
    val name: String,
    val isDefault: Boolean = false
)

@Composable
fun VariationSelector(
    variations: List<VariationOption>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        variations.forEach { variation ->
            val selected = variation.id == selectedId
            FilterChip(
                selected = selected,
                onClick = { onSelect(variation.id) },
                label = { Text(variation.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
