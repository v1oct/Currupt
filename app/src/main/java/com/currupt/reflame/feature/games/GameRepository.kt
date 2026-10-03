package com.currupt.reflame.feature.games

import com.currupt.reflame.core.model.Game

interface GameRepository {
    suspend fun getGames(): List<Game>
    suspend fun getGame(id: String): Game?
}
