package com.currupt.reflame.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MaintenanceState(
    @SerialName("is_under_maintenance") val isUnderMaintenance: Boolean = false,
    val message: String = "",
    @SerialName("allowed_roles") val allowedRoles: List<String> = emptyList()
)

@Serializable
data class BrandingAssets(
    @SerialName("logo_url") val logoUrl: String = "",
    @SerialName("splash_image_url") val splashImageUrl: String = "",
    @SerialName("accent_color_hex") val accentColorHex: String = "",
    @SerialName("loading_animation_url") val loadingAnimationUrl: String = ""
)

@Serializable
data class AnnouncementsConfig(
    @SerialName("is_enabled") val isEnabled: Boolean = true,
    @SerialName("active_announcement_ids") val activeAnnouncementIds: List<String> = emptyList()
)

@Serializable
data class ClientConfig(
    val metadata: ConfigMetadata = ConfigMetadata(),
    @SerialName("maintenance_state") val maintenanceState: MaintenanceState = MaintenanceState(),
    @SerialName("operational_mode_config") val operationalModeConfig: OperationalModeConfig = OperationalModeConfig(),
    @SerialName("current_configuration") val currentConfiguration: JsonObject = JsonObject(emptyMap()),
    @SerialName("feature_flags") val featureFlags: List<FeatureFlag> = emptyList(),
    @SerialName("announcements_config") val announcementsConfig: AnnouncementsConfig = AnnouncementsConfig(),
    @SerialName("branding_assets") val brandingAssets: BrandingAssets = BrandingAssets()
) {
    fun getEffectiveOperationalModeConfig(): OperationalModeConfig {
        if (operationalModeConfig.isEnabled) {
            return operationalModeConfig
        }
        if (maintenanceState.isUnderMaintenance) {
            return OperationalModeConfig(
                mode = OperationalMode.MAINTENANCE,
                isEnabled = true,
                title = "Maintenance in Progress",
                message = maintenanceState.message
            )
        }
        return OperationalModeConfig(
            mode = OperationalMode.NORMAL,
            isEnabled = true
        )
    }
}
