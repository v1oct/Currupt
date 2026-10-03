package com.currupt.reflame.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ToolCategory {
    PERFORMANCE,
    GRAPHICS,
    UTILITY,
    NETWORK,
    SYSTEM
}

@Serializable
data class Tool(
    val id: String,
    @SerialName("display_name") val displayName: String,
    val description: String = "",
    val category: ToolCategory = ToolCategory.UTILITY,
    @SerialName("is_enabled") val isEnabled: Boolean = true,
    @SerialName("is_premium") val isPremium: Boolean = false
)
