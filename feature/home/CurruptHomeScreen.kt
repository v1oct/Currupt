package com.currupt.reflame.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.account.state.AccountState
import com.currupt.reflame.client.model.OperationalMode
import com.currupt.reflame.client.model.OperationalModeConfig
import com.currupt.reflame.client.runtime.ClientRuntimeManager
import com.currupt.reflame.client.runtime.ClientRuntimeStatus
import com.currupt.reflame.client.runtime.PermissionStatus
import com.currupt.reflame.core.config.ConfigManager
import com.currupt.reflame.feature.games.GameManager
import com.currupt.reflame.feature.games.detection.GameDetectionManager
import com.currupt.reflame.feature.games.detection.GameDetectionResult
import com.currupt.reflame.ui.design.CurruptColors
import com.currupt.reflame.ui.design.CurruptTypography

@Composable
fun CurruptHomeScreen(
    runtimeManager: ClientRuntimeManager,
    configManager: ConfigManager,
    gameManager: GameManager,
    detectionManager: GameDetectionManager,
    accountState: AccountState,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val runtimeState by runtimeManager.runtimeState.collectAsState()
    val permissionStatus by runtimeManager.permissionStatus.collectAsState()
    val opModeConfig by configManager.operationalModeConfig.collectAsState()
    val gameState by gameManager.state.collectAsState()
    val detectionResult by detectionManager.detectionResult.collectAsState()

    val currentEntitlement = if (accountState is AccountState.Authenticated) {
        accountState.account.entitlement
    } else {
        UserEntitlement.FREE
    }

    LaunchedEffect(Unit) {
        gameManager.loadGames()
        detectionManager.performDetection()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. TOP AREA HEADER
        CurruptTopHeader(
            accountState = accountState,
            entitlement = currentEntitlement
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. OPERATIONAL MODE NOTICE BANNER (IF NOT NORMAL)
        if (opModeConfig.isEnabled && opModeConfig.mode != OperationalMode.NORMAL) {
            OperationalModeNoticeCard(config = opModeConfig)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. ACTIVE GAME CARD (ROBLOX)
        ActiveGameCard(
            runtimeStatus = runtimeState.status,
            permissionStatus = permissionStatus,
            opModeConfig = opModeConfig,
            detectionResult = detectionResult,
            entitlement = currentEntitlement,
            onActionClick = {
                if (permissionStatus == PermissionStatus.REQUIRED) {
                    runtimeManager.openOverlayPermissionSettings()
                } else if (runtimeState.status == ClientRuntimeStatus.RUNNING) {
                    runtimeManager.togglePanel()
                } else {
                    kotlinx.coroutines.GlobalScope.run {
                        runtimeManager.startRuntime()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4. WHAT'S NEW CARD
        WhatsNewCard()

        Spacer(modifier = Modifier.height(20.dp))

        // 5. FEATURED PROMOTIONAL BANNER
        FeaturedPromotionalCard(entitlement = currentEntitlement)

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun CurruptTopHeader(
    accountState: AccountState,
    entitlement: UserEntitlement
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "CURRUPT",
                style = CurruptTypography.BrandHeader
            )
            Text(
                text = "Identity Gaming Client",
                style = CurruptTypography.Subtitle
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Entitlement Pill
            val isPremium = entitlement == UserEntitlement.PREMIUM
            val pillBorder = if (isPremium) CurruptColors.AccentGold else CurruptColors.BorderBright
            val pillText = if (isPremium) "PREMIUM" else "FREE"

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, pillBorder, RoundedCornerShape(12.dp)),
                color = if (isPremium) CurruptColors.AccentGold.copy(alpha = 0.15f) else CurruptColors.SurfaceDark
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isPremium) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = CurruptColors.AccentGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = pillText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPremium) CurruptColors.AccentGold else CurruptColors.TextSecondary
                    )
                }
            }

            // User Avatar Indicator
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, CurruptColors.BorderBright, CircleShape),
                color = CurruptColors.SurfaceDark
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = if (accountState is AccountState.Authenticated) {
                        accountState.account.displayName.take(1).uppercase().ifBlank { "C" }
                    } else {
                        "C"
                    }
                    Text(
                        text = initial,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CurruptColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveGameCard(
    runtimeStatus: ClientRuntimeStatus,
    permissionStatus: PermissionStatus,
    opModeConfig: OperationalModeConfig,
    detectionResult: GameDetectionResult,
    entitlement: UserEntitlement,
    onActionClick: () -> Unit
) {
    val isEmergency = opModeConfig.isEnabled && opModeConfig.mode == OperationalMode.EMERGENCY
    val isUpdateRequired = opModeConfig.isEnabled && opModeConfig.mode == OperationalMode.UPDATE_REQUIRED
    val isPermissionMissing = permissionStatus == PermissionStatus.REQUIRED

    val activeAccent = CurruptColors.getAccentForEntitlement(entitlement)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CurruptColors.BorderBright.run { 20.dp }))
            .border(1.dp, CurruptColors.BorderBright, RoundedCornerShape(20.dp)),
        color = CurruptColors.CardSurface
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Roblox Logo / Icon Placeholder
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f)),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "R",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Roblox",
                            style = CurruptTypography.CardTitle
                        )
                        Text(
                            text = "com.roblox.client",
                            style = CurruptTypography.Caption
                        )
                    }
                }

                // Status Badge
                val (statusText, statusColor) = when {
                    isEmergency -> "EMERGENCY" to CurruptColors.StatusRed
                    isUpdateRequired -> "UPDATE REQ" to CurruptColors.StatusYellow
                    isPermissionMissing -> "PERMISSION" to CurruptColors.StatusYellow
                    runtimeStatus == ClientRuntimeStatus.RUNNING -> "ACTIVE" to CurruptColors.StatusGreen
                    else -> "READY V1" to CurruptColors.StatusGreen
                }

                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Line
            Text(
                text = when {
                    isPermissionMissing -> "Overlay permission (SYSTEM_ALERT_WINDOW) is required for the client panel."
                    runtimeStatus == ClientRuntimeStatus.RUNNING -> "CURRUPT runtime active. Edge handle is visible on screen edge."
                    else -> "Roblox V1 runtime ready. Launch to open client overlay panel."
                },
                style = CurruptTypography.Body
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action Button
            val buttonEnabled = !isEmergency && !isUpdateRequired
            val buttonText = when {
                isPermissionMissing -> "GRANT OVERLAY PERMISSION"
                runtimeStatus == ClientRuntimeStatus.RUNNING -> "TOGGLE CLIENT PANEL"
                isEmergency || isUpdateRequired -> "LAUNCH DISABLED"
                else -> "LAUNCH ROBLOX CLIENT"
            }

            Button(
                onClick = onActionClick,
                enabled = buttonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPermissionMissing) CurruptColors.StatusYellow else activeAccent,
                    contentColor = if (entitlement == UserEntitlement.PREMIUM && !isPermissionMissing) Color.Black else Color.Black
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isPermissionMissing) Icons.Rounded.Security else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OperationalModeNoticeCard(config: OperationalModeConfig) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CurruptColors.StatusYellow.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        color = CurruptColors.StatusYellow.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = CurruptColors.StatusYellow,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = config.title.ifBlank { "System Notice: ${config.mode.name}" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CurruptColors.TextPrimary
                )
                if (config.message.isNotBlank()) {
                    Text(
                        text = config.message,
                        fontSize = 12.sp,
                        color = CurruptColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun WhatsNewCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CurruptColors.BorderSubtle, RoundedCornerShape(16.dp)),
        color = CurruptColors.CardSurface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WHAT'S NEW",
                    style = CurruptTypography.SectionHeader
                )
                Text(
                    text = "v1.0.0",
                    style = CurruptTypography.Caption
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val updates = listOf(
                "Roblox V1 Launch & Game Detection",
                "CURRUPT Edge Handle Runtime Overlay",
                "Persistent Config Cache & Fail-Safe Remote Sync",
                "Advanced Operational Modes (Maintenance, Emergency)",
                "Premium Entitlement Framework"
            )

            updates.forEach { update ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = CurruptColors.TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = update,
                        style = CurruptTypography.Body
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedPromotionalCard(entitlement: UserEntitlement) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CurruptColors.BorderSubtle, RoundedCornerShape(16.dp)),
        color = CurruptColors.CardSurface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF181820),
                            Color(0xFF101014)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "CURRUPT GAMING",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = CurruptColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PLAY SMARTER. STAY AHEAD.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = CurruptColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Modular gaming client runtime for Android.",
                    style = CurruptTypography.Caption
                )
            }
        }
    }
}
