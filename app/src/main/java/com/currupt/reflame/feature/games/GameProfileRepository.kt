package com.currupt.reflame.feature.games

import com.currupt.reflame.core.model.GameProfile

interface GameProfileRepository {
    suspend fun getGameProfile(gameId: String): GameProfile?
    suspend fun getGameProfiles(): List<GameProfile>
}
