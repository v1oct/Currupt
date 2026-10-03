package com.currupt.reflame.feature.tools

import com.currupt.reflame.core.model.Tool

data class ToolFeatureState(
    val isLoading: Boolean = false,
    val availableTools: List<Tool> = emptyList(),
    val selectedTool: Tool? = null,
    val toolAvailabilityMap: Map<String, ToolAvailability> = emptyMap(),
    val errorMessage: String? = null
)
