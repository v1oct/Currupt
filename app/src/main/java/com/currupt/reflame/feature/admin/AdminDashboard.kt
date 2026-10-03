package com.currupt.reflame.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.currupt.reflame.ui.motion.MotionSystem

@Composable
fun AdminDashboard(
    onNavigateToContent: () -> Unit,
    onNavigateToCanvas: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToMedia: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        when (val state = uiState) {
            is AdminState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
            is AdminState.Unauthenticated -> {
                AdminLoginScreen(
                    onLoginSuccess = { viewModel.checkAuthAndLoadDashboard() }
                )
            }
            is AdminState.Unauthorized -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Access Denied.", color = Color.Red, style = MaterialTheme.typography.headlineSmall)
                        Text("Your account is not an authorized admin.", color = Color.White.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(24.dp))
                        TextButton(onClick = { viewModel.logout {} }) {
                            Text("LOGOUT", color = Color.White)
                        }
                    }
                }
            }
            is AdminState.Dashboard -> {
                DashboardContent(
                    state = state,
                    onNavigateToContent = onNavigateToContent,
                    onNavigateToCanvas = onNavigateToCanvas,
                    onNavigateToCategories = onNavigateToCategories,
                    onNavigateToMedia = onNavigateToMedia,
                    onLogout = { viewModel.logout {} }
                )
            }
            is AdminState.Error -> {
                Text("Error: ${state.message}", color = Color.Red, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: AdminState.Dashboard,
    onNavigateToContent: () -> Unit,
    onNavigateToCanvas: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToMedia: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        MotionSystem.EntranceTransition {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STUDIO CMS",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Manage your CURRUPT. canvas.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout", tint = Color.White.copy(alpha = 0.4f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Overview
        MotionSystem.EntranceTransition(delayMillis = 100) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(label = "CONTENT", value = state.totalContent.toString(), modifier = Modifier.weight(1f))
                StatCard(label = "PUBLISHED", value = state.publishedContent.toString(), modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Actions
        MotionSystem.EntranceTransition(delayMillis = 200) {
            Text(
                text = "MANAGEMENT",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.4f),
                    letterSpacing = 2.sp
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        val actions = listOf(
            AdminAction("Content", Icons.Rounded.Description, onNavigateToContent),
            AdminAction("Canvas", Icons.Rounded.Layers, onNavigateToCanvas),
            AdminAction("Categories", Icons.Rounded.Category, onNavigateToCategories),
            AdminAction("Media", Icons.Rounded.PermMedia, onNavigateToMedia)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(actions) { index, action ->
                MotionSystem.ScrollReveal(index = index) {
                    ActionCard(
                        title = action.title,
                        icon = action.icon,
                        onClick = action.onClick
                    )
                }
            }
        }
    }
}

private data class AdminAction(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(24.dp)),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
