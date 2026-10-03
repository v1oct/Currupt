package com.currupt.reflame.feature.tools

import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.core.model.GameProfile
import com.currupt.reflame.core.model.Tool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ToolManager(
    private val registry: ToolRegistry = ToolRegistry()
) {
    private val _state = MutableStateFlow(ToolFeatureState(isLoading = true))
    val state: StateFlow<ToolFeatureState> = _state.asStateFlow()

    suspend fun loadTools(
        gameProfile: GameProfile? = null,
        userEntitlement: UserEntitlement = UserEntitlement.FREE
    ) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val tools = registry.getRegisteredTools()
            val availabilityMap = tools.associate { tool ->
                tool.id to ToolAvailabilityResolver.resolveAvailability(
                    tool = tool,
                    gameProfile = gameProfile,
                    userEntitlement = userEntitlement
                )
            }

            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    availableTools = tools,
                    toolAvailabilityMap = availabilityMap,
                    selectedTool = currentState.selectedTool ?: tools.firstOrNull()
                )
            }
        } catch (e: Throwable) {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load registered tools"
                )
            }
        }
    }

    suspend fun selectTool(toolId: String): Boolean {
        val tool = registry.getToolById(toolId)
        return if (tool != null) {
            _state.update { it.copy(selectedTool = tool) }
            true
        } else {
            false
        }
    }

    fun selectTool(tool: Tool) {
        _state.update { it.copy(selectedTool = tool) }
    }
}
