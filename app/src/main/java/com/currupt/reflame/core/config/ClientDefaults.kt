package com.currupt.reflame.core.config

import com.currupt.reflame.client.model.AnnouncementsConfig
import com.currupt.reflame.client.model.BrandingAssets
import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.client.model.FeatureFlag
import com.currupt.reflame.client.model.MaintenanceState
import com.currupt.reflame.client.model.OperationalMode
import com.currupt.reflame.client.model.OperationalModeConfig

object ClientDefaults {
    val DEFAULT_FEATURE_FLAGS = listOf(
        FeatureFlag(key = "enable_performance_monitoring", enabled = true),
        FeatureFlag(key = "enable_game_tools", enabled = true),
        FeatureFlag(key = "enable_profile_customization", enabled = true)
    )

    val DEFAULT_BRANDING = BrandingAssets(
        logoUrl = "",
        splashImageUrl = "",
        accentColorHex = "#FF0000",
        loadingAnimationUrl = ""
    )

    val DEFAULT_OPERATIONAL_MODE_CONFIG = OperationalModeConfig(
        mode = OperationalMode.NORMAL,
        isEnabled = true,
        title = "Normal Operation",
        message = "All services operational."
    )

    val DEFAULT_CLIENT_CONFIG = ClientConfig(
        maintenanceState = MaintenanceState(isUnderMaintenance = false),
        operationalModeConfig = DEFAULT_OPERATIONAL_MODE_CONFIG,
        featureFlags = DEFAULT_FEATURE_FLAGS,
        announcementsConfig = AnnouncementsConfig(isEnabled = true),
        brandingAssets = DEFAULT_BRANDING
    )
}
