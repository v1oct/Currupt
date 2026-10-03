package com.currupt.reflame.core.config

import com.currupt.reflame.client.model.AnnouncementsConfig
import com.currupt.reflame.client.model.BrandingAssets
import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.client.model.FeatureFlag
import com.currupt.reflame.client.model.MaintenanceState

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

    val DEFAULT_CLIENT_CONFIG = ClientConfig(
        maintenanceState = MaintenanceState(isUnderMaintenance = false),
        featureFlags = DEFAULT_FEATURE_FLAGS,
        announcementsConfig = AnnouncementsConfig(isEnabled = true),
        brandingAssets = DEFAULT_BRANDING
    )
}
