package com.currupt.reflame.feature.admin.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun EditorField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedIndicatorColor = Color.White.copy(alpha = 0.1f),
            focusedIndicatorColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
            focusedLabelColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White
        )
    )
}

@Composable
fun filterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Color.Transparent,
    labelColor = Color.White.copy(alpha = 0.4f),
    selectedContainerColor = Color.White.copy(alpha = 0.1f),
    selectedLabelColor = Color.White
)
