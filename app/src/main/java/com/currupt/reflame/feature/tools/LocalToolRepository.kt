package com.currupt.reflame.feature.tools

import com.currupt.reflame.core.model.Tool
import com.currupt.reflame.core.model.ToolCategory

class LocalToolRepository : ToolRepository {

    private val localTools = listOf(
        Tool(
            id = "fps_monitor",
            displayName = "FPS Monitor",
            description = "Real-time frame rate display.",
            category = ToolCategory.PERFORMANCE,
            isEnabled = true,
            isPremium = false
        ),
        Tool(
            id = "fps_cap",
            displayName = "FPS Cap",
            description = "Frame rate limiter configuration.",
            category = ToolCategory.PERFORMANCE,
            isEnabled = true,
            isPremium = false
        ),
        Tool(
            id = "motion_blur",
            displayName = "Motion Blur",
            description = "Visual motion smoothing configuration.",
            category = ToolCategory.GRAPHICS,
            isEnabled = true,
            isPremium = true
        ),
        Tool(
            id = "black_screen",
            displayName = "Black Screen Saver",
            description = "OLED battery saver display mode.",
            category = ToolCategory.UTILITY,
            isEnabled = true,
            isPremium = false
        ),
        Tool(
            id = "cps_counter",
            displayName = "CPS Counter",
            description = "Clicks per second counter.",
            category = ToolCategory.UTILITY,
            isEnabled = true,
            isPremium = false
        ),
        Tool(
            id = "audio_boost",
            displayName = "Audio Boost",
            description = "Sound equalizer profile configuration.",
            category = ToolCategory.SYSTEM,
            isEnabled = true,
            isPremium = true
        ),
        Tool(
            id = "device_cooler",
            displayName = "Device Cooler",
            description = "Thermal profile and background optimization.",
            category = ToolCategory.SYSTEM,
            isEnabled = true,
            isPremium = false
        )
    )

    override suspend fun getTools(): List<Tool> {
        return localTools
    }

    override suspend fun getTool(id: String): Tool? {
        return localTools.find { it.id == id }
    }
}
