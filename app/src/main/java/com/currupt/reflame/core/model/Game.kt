package com.currupt.reflame.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("package_names") val packageNames: List<String> = emptyList(),
    @SerialName("icon_url") val iconUrl: String? = null,
    @SerialName("branding_url") val brandingUrl: String? = null,
    @SerialName("is_enabled") val isEnabled: Boolean = true
)
