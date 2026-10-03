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
    REQUIRES_PREMIUM
}

@Serializable
data class ToolAvailability(
    val toolId: String,
    val status: ToolAvailabilityStatus = ToolAvailabilityStatus.AVAILABLE,
    val reason: String? = null
)

object ToolAvailabilityResolver {
    fun resolveAvailability(
        tool: Tool,
        gameProfile: GameProfile? = null,
        userEntitlement: UserEntitlement = UserEntitlement.FREE
    ): ToolAvailability {
        if (!tool.isEnabled) {
            return ToolAvailability(
                toolId = tool.id,
                status = ToolAvailabilityStatus.DISABLED_GLOBALLY,
                reason = "Tool is globally disabled."
            )
        }

        if (gameProfile != null) {
            val isEnabledForGame = gameProfile.enabledTools.contains(tool.id)
            if (!isEnabledForGame) {
                return ToolAvailability(
                    toolId = tool.id,
                    status = ToolAvailabilityStatus.UNAVAILABLE_FOR_GAME,
                    reason = "Tool is not enabled for the selected game."
                )
            }
        }

        if (tool.isPremium && userEntitlement != UserEntitlement.PREMIUM) {
            return ToolAvailability(
                toolId = tool.id,
                status = ToolAvailabilityStatus.REQUIRES_PREMIUM,
                reason = "Tool requires Premium entitlement."
            )
        }

        return ToolAvailability(
            toolId = tool.id,
            status = ToolAvailabilityStatus.AVAILABLE
        )
    }
}
