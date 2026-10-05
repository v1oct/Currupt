package com.currupt.reflame.feature.tools

import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.core.model.GameProfile
import com.currupt.reflame.core.model.Tool
import kotlinx.serialization.Serializable

@Serializable
enum class ToolAvailabilityStatus {
    AVAILABLE,
    DISABLED_GLOBALLY,
    UNAVAILABLE_FOR_GAME,
    GAME_DISABLED,
    REQUIRES_PREMIUM,
    UNKNOWN_TOOL
}

@Serializable
data class ToolAvailability(
    val toolId: String,
    val status: ToolAvailabilityStatus = ToolAvailabilityStatus.AVAILABLE,
    val reason: String? = null
)

object ToolAvailabilityResolver {

    fun resolveAvailability(
        tool: Tool?,
        toolId: String,
        gameProfile: GameProfile? = null,
        userEntitlement: UserEntitlement = UserEntitlement.FREE
    ): ToolAvailability {
        if (tool == null) {
            return ToolAvailability(
                toolId = toolId,
                status = ToolAvailabilityStatus.UNKNOWN_TOOL,
                reason = "Tool with ID '$toolId' is not registered in ToolRegistry."
            )
        }

        if (!tool.isEnabled) {
            return ToolAvailability(
                toolId = tool.id,
                status = ToolAvailabilityStatus.DISABLED_GLOBALLY,
                reason = "Tool '${tool.displayName}' is globally disabled."
            )
        }

        if (gameProfile != null) {
            if (!gameProfile.isEnabled) {
                return ToolAvailability(
                    toolId = tool.id,
                    status = ToolAvailabilityStatus.GAME_DISABLED,
                    reason = "Game profile for '${gameProfile.gameId}' is disabled."
                )
            }

            val isToolAssignedToGame = gameProfile.enabledTools.contains(tool.id)
            if (!isToolAssignedToGame) {
                return ToolAvailability(
                    toolId = tool.id,
                    status = ToolAvailabilityStatus.UNAVAILABLE_FOR_GAME,
                    reason = "Tool '${tool.displayName}' is not assigned to game '${gameProfile.gameId}'."
                )
            }
        }

        if (tool.isPremium && userEntitlement != UserEntitlement.PREMIUM) {
            return ToolAvailability(
                toolId = tool.id,
                status = ToolAvailabilityStatus.REQUIRES_PREMIUM,
                reason = "Tool '${tool.displayName}' requires Premium entitlement."
            )
        }

        return ToolAvailability(
            toolId = tool.id,
            status = ToolAvailabilityStatus.AVAILABLE
        )
    }

    fun resolveAvailability(
        tool: Tool,
        gameProfile: GameProfile? = null,
        userEntitlement: UserEntitlement = UserEntitlement.FREE
    ): ToolAvailability {
        return resolveAvailability(
            tool = tool,
            toolId = tool.id,
            gameProfile = gameProfile,
            userEntitlement = userEntitlement
        )
    }
}
