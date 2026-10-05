package com.currupt.reflame.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ModeButtonConfig(
    val label: String,
    val action: String? = null,
    @SerialName("target_url") val targetUrl: String? = null
)

@Serializable
data class OperationalModeConfig(
    val mode: OperationalMode = OperationalMode.NORMAL,
    @SerialName("is_enabled") val isEnabled: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val message: String = "",
    @SerialName("logo_url") val logoUrl: String = "",
    @SerialName("image_url") val imageUrl: String = "",
    @SerialName("animation_url") val animationUrl: String = "",
    @SerialName("primary_button") val primaryButton: ModeButtonConfig? = null,
    @SerialName("secondary_button") val secondaryButton: ModeButtonConfig? = null,
    @SerialName("estimated_return_time") val estimatedReturnTime: String? = null,
    @SerialName("custom_settings") val customSettings: JsonObject = JsonObject(emptyMap())
)
