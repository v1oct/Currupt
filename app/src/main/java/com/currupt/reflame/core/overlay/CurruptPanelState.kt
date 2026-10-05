package com.currupt.reflame.core.overlay

import com.currupt.reflame.core.model.Tool
import kotlinx.serialization.Serializable

@Serializable
enum class CurruptPanelTab {
    HOME,
    TOOLS,
    VISUALS,
    AUDIO,
    SETTINGS
}

data class CurruptPanelUiState(
    val activeTab: CurruptPanelTab = CurruptPanelTab.HOME,
    val selectedToolForDetail: Tool? = null,
    val searchQuery: String = "",
    
    // Tool Toggles
    val fpsMonitorEnabled: Boolean = false,
    val motionBlurEnabled: Boolean = false,
    val cpsCounterEnabled: Boolean = false,
    
    // Motion Blur Settings
    val motionBlurIntensity: Float = 0.6f,
    val motionBlurDurationMs: Int = 180,
    
    // Panel Preferences
    val panelOpacity: Float = 0.95f,
    val compactMode: Boolean = false
)
