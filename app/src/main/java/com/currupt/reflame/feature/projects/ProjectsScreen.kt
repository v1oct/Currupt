package com.currupt.reflame.feature.projects

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.currupt.reflame.core.model.ContentType
import com.currupt.reflame.feature.home.ContentCard
import com.currupt.reflame.ui.motion.MotionSystem

@Composable
fun ProjectsScreen(
    onContentClick: (String) -> Unit,
    viewModel: CatalogViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedType by remember { mutableStateOf<ContentType?>(null) }
    
    val filteredContent = remember(selectedType, items) {
        if (selectedType == null) items
        else items.filter { it.contentType == selectedType }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // Page Header
        MotionSystem.EntranceTransition {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "CATALOG",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Browse the CURRUPT. collection.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.5f)
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Filter Chips Row
        MotionSystem.EntranceTransition(delayMillis = 100) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    label = { Text("ALL") },
                    colors = filterChipColors()
                )
                ContentType.entries.filter { it != ContentType.ANNOUNCEMENT }.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.name) },
                        colors = filterChipColors()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isLoading && items.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                itemsIndexed(filteredContent) { index, item ->
                    MotionSystem.ScrollReveal(index = index) {
                        ContentCard(
                            content = item,
                            onClick = { onContentClick(item.slug) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun filterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Color.Transparent,
    labelColor = Color.White.copy(alpha = 0.4f),
    selectedContainerColor = Color.White.copy(alpha = 0.1f),
    selectedLabelColor = Color.White,
    selectedLeadingIconColor = Color.White,
    iconColor = Color.White.copy(alpha = 0.4f)
)
