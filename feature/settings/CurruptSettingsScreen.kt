package com.currupt.reflame.feature.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.currupt.reflame.account.state.AccountState
import com.currupt.reflame.client.runtime.ClientRuntimeManager
import com.currupt.reflame.client.runtime.PermissionStatus
import com.currupt.reflame.core.config.ConfigManager
import com.currupt.reflame.feature.games.detection.GameDetectionManager
import com.currupt.reflame.ui.design.CurruptColors
import com.currupt.reflame.ui.design.CurruptTypography

@Composable
fun CurruptSettingsScreen(
    runtimeManager: ClientRuntimeManager,
    configManager: ConfigManager,
    detectionManager: GameDetectionManager,
    accountState: AccountState,
    modifier: Modifier = Modifier
) {
    val runtimeState by runtimeManager.runtimeState.collectAsState()
    val permissionStatus by runtimeManager.permissionStatus.collectAsState()
    val detectionResult by detectionManager.detectionResult.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SETTINGS",
            style = CurruptTypography.BrandHeader
        )
        Text(
            text = "Application & Runtime Preferences",
            style = CurruptTypography.Subtitle
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 1. GENERAL SECTION
        SettingsSectionHeader(title = "GENERAL", icon = Icons.Rounded.Palette)
        SettingsCard {
            var notificationsEnabled by remember { mutableStateOf(true) }
            
            SettingsRow(
                label = "Theme",
                value = "Dark / Gold Accent"
            )
            SettingsRowToggle(
                label = "Notifications",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            SettingsRow(
                label = "Background Rendering",
                value = "Ambient Gradient"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. CLIENT & RUNTIME SECTION
        SettingsSectionHeader(title = "CLIENT & RUNTIME", icon = Icons.Rounded.Security)
        SettingsCard {
            SettingsRow(
                label = "Runtime Status",
                value = runtimeState.status.name
            )
            SettingsRow(
                label = "Overlay Permission",
                value = permissionStatus.name
            )
            if (permissionStatus == PermissionStatus.REQUIRED) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { runtimeManager.openOverlayPermissionSettings() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CurruptColors.StatusYellow)
                ) {
                    Text(
                        text = "GRANT OVERLAY PERMISSION",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            SettingsRow(
                label = "Game Detection Engine",
                value = detectionResult.status.name
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. ACCOUNT SECTION
        SettingsSectionHeader(title = "ACCOUNT & ENTITLEMENT", icon = Icons.Rounded.AccountCircle)
        SettingsCard {
            val accountName = if (accountState is AccountState.Authenticated) {
                accountState.account.displayName
            } else {
                "Local Developer"
            }
            val entitlementText = if (accountState is AccountState.Authenticated) {
                accountState.account.entitlement.name
            } else {
                "FREE"
            }

            SettingsRow(
                label = "Display Name",
                value = accountName
            )
            SettingsRow(
                label = "Entitlement Tier",
                value = entitlementText
            )
            SettingsRow(
                label = "Discord Account",
                value = "Not Connected (Post-Beta)"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. ABOUT SECTION
        SettingsSectionHeader(title = "ABOUT", icon = Icons.Rounded.Info)
        SettingsCard {
            SettingsRow(label = "Version", value = "v1.0.0")
            SettingsRow(label = "Build", value = "1.0.0-release")
            SettingsRow(label = "Client Architecture", value = "CURRUPT Gaming V1")
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CurruptColors.TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = CurruptTypography.SectionHeader
        )
    }
}

@Composable
private fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CurruptColors.BorderSubtle, RoundedCornerShape(16.dp)),
        color = CurruptColors.CardSurface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = CurruptTypography.Body
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CurruptColors.TextPrimary
        )
    }
}

@Composable
private fun SettingsRowToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = CurruptTypography.Body
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CurruptColors.BorderBright,
                uncheckedThumbColor = CurruptColors.TextMuted,
                uncheckedTrackColor = CurruptColors.SurfaceDark
            )
        )
    }
}
