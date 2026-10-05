package com.currupt.reflame.feature.games

import com.currupt.reflame.core.model.GameProfile

class LocalGameProfileRepository : GameProfileRepository {

    private val profiles = mapOf(
        "minecraft" to GameProfile(
            gameId = "minecraft",
            enabledTools = listOf("fps_monitor", "fps_cap", "motion_blur"),
            profileVersion = 1L,
            isEnabled = true
        ),
        "roblox" to GameProfile(
            gameId = "roblox",
            enabledTools = listOf("fps_monitor", "motion_blur", "cps_counter"),
            profileVersion = 1L,
            isEnabled = true
        ),
        "fc_mobile" to GameProfile(
            gameId = "fc_mobile",
            enabledTools = listOf("fps_monitor", "black_screen"),
            profileVersion = 1L,
            isEnabled = true
        )
    )

    override suspend fun getGameProfile(gameId: String): GameProfile? {
        return profiles[gameId]
    }

    override suspend fun getGameProfiles(): List<GameProfile> {
        return profiles.values.toList()
    }
}
