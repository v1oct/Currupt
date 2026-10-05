package com.currupt.reflame.core.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.client.model.OperationalMode
import com.currupt.reflame.client.model.OperationalModeConfig
import com.currupt.reflame.core.model.GameProfile
import com.currupt.reflame.core.model.Tool
import com.currupt.reflame.feature.games.LocalGameProfileRepository
import com.currupt.reflame.feature.games.detection.DetectionStatus
import com.currupt.reflame.feature.games.detection.GameDetectionResult
import com.currupt.reflame.feature.tools.ToolAvailability
import com.currupt.reflame.feature.tools.ToolAvailabilityResolver
import com.currupt.reflame.feature.tools.ToolAvailabilityStatus
import com.currupt.reflame.feature.tools.ToolManager
import com.currupt.reflame.ui.design.CurruptColors
import com.currupt.reflame.ui.design.CurruptTypography

@Composable
fun CurruptEdgePanelContent(
    gameDetectionResult: GameDetectionResult?,
    opModeConfig: OperationalModeConfig?,
    userEntitlement: UserEntitlement = UserEntitlement.FREE,
    onClosePanel: () -> Unit
) {
    var uiState by remember { mutableStateOf(CurruptPanelUiState()) }
    val toolManager = remember { ToolManager() }
    val toolState by toolManager.state.collectAsState()
    val profileRepository = remember { LocalGameProfileRepository() }
    var activeProfile by remember { mutableStateOf<GameProfile?>(null) }

    LaunchedEffect(Unit) {
        val robloxProfile = profileRepository.getGameProfile("roblox")
        activeProfile = robloxProfile
        toolManager.loadTools(
            gameProfile = robloxProfile,
            userEntitlement = userEntitlement
        )
    }

    val isPremium = userEntitlement == UserEntitlement.PREMIUM
    val accentColor = if (isPremium) CurruptColors.AccentGold else CurruptColors.TextPrimary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        // Floating Panel Container
        Surface(
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight(0.9f)
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CurruptColors.BorderBright, RoundedCornerShape(20.dp)),
            color = CurruptColors.SurfaceDark.copy(alpha = uiState.panelOpacity)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // PANEL HEADER
                PanelHeader(
                    gameDetectionResult = gameDetectionResult,
                    entitlement = userEntitlement,
                    onClosePanel = onClosePanel
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CATEGORY NAV TABS
                PanelNavTabs(
                    activeTab = uiState.activeTab,
                    onTabSelected = { tab ->
                        uiState = uiState.copy(activeTab = tab, selectedToolForDetail = null)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // TAB CONTENT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (uiState.selectedToolForDetail != null) {
                        ToolDetailView(
                            tool = uiState.selectedToolForDetail!!,
                            uiState = uiState,
                            onUpdateState = { updated -> uiState = updated },
                            onBack = { uiState = uiState.copy(selectedToolForDetail = null) }
                        )
                    } else {
                        when (uiState.activeTab) {
                            CurruptPanelTab.HOME -> HomeTabContent(
                                gameDetectionResult = gameDetectionResult,
                                opModeConfig = opModeConfig,
                                entitlement = userEntitlement,
                                availableToolCount = toolState.availableTools.size
                            )
                            CurruptPanelTab.TOOLS -> ToolsTabContent(
                                availableTools = toolState.availableTools,
                                toolAvailabilityMap = toolState.toolAvailabilityMap,
                                uiState = uiState,
                                onUpdateState = { updated -> uiState = updated }
                            )
                            CurruptPanelTab.VISUALS -> VisualsTabContent(
                                availableTools = toolState.availableTools,
                                toolAvailabilityMap = toolState.toolAvailabilityMap,
                                uiState = uiState,
                                onUpdateState = { updated -> uiState = updated }
                            )
                            CurruptPanelTab.AUDIO -> AudioTabContent()
                            CurruptPanelTab.SETTINGS -> SettingsTabContent(
                                uiState = uiState,
                                onUpdateState = { updated -> uiState = updated },
                                onClosePanel = onClosePanel
                            )
                        }
                    }
                }
            }
        }

        // FLOATING WIDGETS
        if (uiState.fpsMonitorEnabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, CurruptColors.StatusGreen, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "60 FPS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CurruptColors.StatusGreen
                )
            }
        }

        if (uiState.cpsCounterEnabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, CurruptColors.StatusBlue, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "0 CPS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CurruptColors.StatusBlue
                )
            }
        }
    }
}

@Composable
private fun PanelHeader(
    gameDetectionResult: GameDetectionResult?,
    entitlement: UserEntitlement,
    onClosePanel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "CURRUPT",
                style = CurruptTypography.BrandHeader.copy(fontSize = 18.sp, letterSpacing = 2.sp)
            )
            val gameName = if (gameDetectionResult?.status == DetectionStatus.SUPPORTED_GAME_DETECTED) {
                gameDetectionResult.game?.displayName ?: "ROBLOX"
            } else {
                "ROBLOX V1"
            }
            Text(
                text = "$gameName • V1 READY",
                style = CurruptTypography.Caption.copy(color = CurruptColors.StatusGreen)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (entitlement == UserEntitlement.PREMIUM) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = CurruptColors.AccentGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            IconButton(
                onClick = onClosePanel,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close Panel",
                    tint = CurruptColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun PanelNavTabs(
    activeTab: CurruptPanelTab,
    onTabSelected: (CurruptPanelTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CurruptColors.CardSurface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CurruptPanelTab.entries.forEach { tab ->
            val isSelected = activeTab == tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) CurruptColors.BorderBright else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.name,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else CurruptColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun HomeTabContent(
    gameDetectionResult: GameDetectionResult?,
    opModeConfig: OperationalModeConfig?,
    entitlement: UserEntitlement,
    availableToolCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (opModeConfig?.isEnabled == true && opModeConfig.mode != OperationalMode.NORMAL) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CurruptColors.StatusYellow, RoundedCornerShape(12.dp)),
                color = CurruptColors.StatusYellow.copy(alpha = 0.1f)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = CurruptColors.StatusYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = opModeConfig.title.ifBlank { opModeConfig.mode.name },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        DashboardInfoCard("CURRENT GAME", "Roblox (V1 Supported)", CurruptColors.StatusGreen)
        Spacer(modifier = Modifier.height(8.dp))
        DashboardInfoCard("CLIENT RUNTIME", "Active & Overlay Connected", CurruptColors.TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        DashboardInfoCard("ASSIGNED TOOLS", "$availableToolCount Available for Roblox", CurruptColors.TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        DashboardInfoCard("ACCOUNT TIER", entitlement.name, CurruptColors.getAccentForEntitlement(entitlement))
    }
}

@Composable
private fun DashboardInfoCard(label: String, value: String, valueColor: Color) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CurruptColors.BorderSubtle, RoundedCornerShape(12.dp)),
        color = CurruptColors.CardSurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = CurruptTypography.Caption)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
private fun ToolsTabContent(
    availableTools: List<Tool>,
    toolAvailabilityMap: Map<String, ToolAvailability>,
    uiState: CurruptPanelUiState,
    onUpdateState: (CurruptPanelUiState) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { onUpdateState(uiState.copy(searchQuery = it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text("Search tools...", fontSize = 12.sp, color = CurruptColors.TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = CurruptColors.TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CurruptColors.BorderBright,
                unfocusedBorderColor = CurruptColors.BorderSubtle,
                focusedContainerColor = CurruptColors.CardSurface,
                unfocusedContainerColor = CurruptColors.CardSurface
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val filteredTools = availableTools.filter { tool ->
            uiState.searchQuery.isBlank() ||
                    tool.displayName.contains(uiState.searchQuery, ignoreCase = true) ||
                    tool.description.contains(uiState.searchQuery, ignoreCase = true) ||
                    tool.category.name.contains(uiState.searchQuery, ignoreCase = true)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredTools.forEach { tool ->
                val availability = toolAvailabilityMap[tool.id]
                ToolItemCard(
                    tool = tool,
                    availability = availability,
                    uiState = uiState,
                    onUpdateState = onUpdateState
                )
            }
        }
    }
}

@Composable
private fun ToolItemCard(
    tool: Tool,
    availability: ToolAvailability?,
    uiState: CurruptPanelUiState,
    onUpdateState: (CurruptPanelUiState) -> Unit
) {
    val isEnabled = when (tool.id) {
        "fps_monitor" -> uiState.fpsMonitorEnabled
        "motion_blur" -> uiState.motionBlurEnabled
        "cps_counter" -> uiState.cpsCounterEnabled
        else -> false
    }

    val isAvailable = availability?.status == ToolAvailabilityStatus.AVAILABLE

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CurruptColors.BorderSubtle, RoundedCornerShape(12.dp)),
        color = CurruptColors.CardSurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val icon = when (tool.id) {
                        "fps_monitor" -> Icons.Rounded.Speed
                        "motion_blur" -> Icons.Rounded.Visibility
                        "cps_counter" -> Icons.Rounded.SportsEsports
                        else -> Icons.Rounded.Settings
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isEnabled) CurruptColors.StatusGreen else CurruptColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tool.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (tool.isPremium) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Premium",
                                    tint = CurruptColors.AccentGold,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Text(
                            text = tool.description,
                            style = CurruptTypography.Caption
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (tool.id == "motion_blur") {
                        IconButton(
                            onClick = { onUpdateState(uiState.copy(selectedToolForDetail = tool)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "Tool Settings",
                                tint = CurruptColors.TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Switch(
                        checked = isEnabled,
                        enabled = isAvailable,
                        onCheckedChange = { checked ->
                            val updated = when (tool.id) {
                                "fps_monitor" -> uiState.copy(fpsMonitorEnabled = checked)
                                "motion_blur" -> uiState.copy(motionBlurEnabled = checked)
                                "cps_counter" -> uiState.copy(cpsCounterEnabled = checked)
                                else -> uiState
                            }
                            onUpdateState(updated)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CurruptColors.StatusGreen
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolDetailView(
    tool: Tool,
    uiState: CurruptPanelUiState,
    onUpdateState: (CurruptPanelUiState) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = CurruptColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Back to Tools",
                fontSize = 12.sp,
                color = CurruptColors.TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = tool.displayName,
            style = CurruptTypography.SectionHeader
        )
        Text(
            text = tool.description,
            style = CurruptTypography.Body
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (tool.id == "motion_blur") {
            Text(
                text = "Intensity: ${(uiState.motionBlurIntensity * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Slider(
                value = uiState.motionBlurIntensity,
                onValueChange = { onUpdateState(uiState.copy(motionBlurIntensity = it)) },
                valueRange = 0.0f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = CurruptColors.StatusGreen
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Duration: ${uiState.motionBlurDurationMs} ms",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Slider(
                value = uiState.motionBlurDurationMs.toFloat(),
                onValueChange = { onUpdateState(uiState.copy(motionBlurDurationMs = it.toInt())) },
                valueRange = 100f..500f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = CurruptColors.StatusGreen
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        onUpdateState(uiState.copy(motionBlurIntensity = 0.6f, motionBlurDurationMs = 180))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CurruptColors.BorderBright)
                ) {
                    Text("RESET", fontSize = 12.sp, color = Color.White)
                }

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = CurruptColors.StatusGreen)
                ) {
                    Text("APPLY", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun VisualsTabContent(
    availableTools: List<Tool>,
    toolAvailabilityMap: Map<String, ToolAvailability>,
    uiState: CurruptPanelUiState,
    onUpdateState: (CurruptPanelUiState) -> Unit
) {
    val visualTools = availableTools.filter { it.category.name == "GRAPHICS" || it.id == "motion_blur" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (visualTools.isEmpty()) {
            Text(text = "No visual tools available.", style = CurruptTypography.Body)
        } else {
            visualTools.forEach { tool ->
                ToolItemCard(
                    tool = tool,
                    availability = toolAvailabilityMap[tool.id],
                    uiState = uiState,
                    onUpdateState = onUpdateState
                )
            }
        }
    }
}

@Composable
private fun AudioTabContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.GraphicEq,
                contentDescription = null,
                tint = CurruptColors.TextMuted,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No audio tools available for active game profile.",
                style = CurruptTypography.Caption
            )
        }
    }
}

@Composable
private fun SettingsTabContent(
    uiState: CurruptPanelUiState,
    onUpdateState: (CurruptPanelUiState) -> Unit,
    onClosePanel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Panel Opacity: ${(uiState.panelOpacity * 100).toInt()}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Slider(
            value = uiState.panelOpacity,
            onValueChange = { onUpdateState(uiState.copy(panelOpacity = it)) },
            valueRange = 0.5f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = CurruptColors.BorderBright
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClosePanel,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CurruptColors.BorderBright)
        ) {
            Text("CLOSE CLIENT PANEL", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
