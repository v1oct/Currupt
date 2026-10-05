package com.currupt.reflame.feature.games

import com.currupt.reflame.core.model.Game

class LocalGameRepository : GameRepository {

    private val localGames = listOf(
        Game(
            id = "minecraft",
            displayName = "Minecraft",
            packageNames = listOf("com.mojang.minecraftpe"),
            iconUrl = null,
            brandingUrl = null,
            isEnabled = true,
            isV1Supported = false
        ),
        Game(
            id = "roblox",
            displayName = "Roblox",
            packageNames = listOf("com.roblox.client"),
            iconUrl = null,
            brandingUrl = null,
            isEnabled = true,
            isV1Supported = true
        ),
        Game(
            id = "fc_mobile",
            displayName = "FC Mobile",
            packageNames = listOf("com.ea.gp.fifamobile"),
            iconUrl = null,
            brandingUrl = null,
            isEnabled = true,
            isV1Supported = false
        )
    )

    override suspend fun getGames(): List<Game> {
        return localGames
    }

    override suspend fun getGame(id: String): Game? {
        return localGames.find { it.id == id }
    }
}
