package com.currupt.reflame.feature.games

import com.currupt.reflame.core.model.Game

class GameRegistry(
    private val repository: GameRepository = LocalGameRepository()
) {
    private val dynamicRegistry = mutableMapOf<String, Game>()

    fun registerGame(game: Game) {
        dynamicRegistry[game.id] = game
    }

    fun registerGames(games: List<Game>) {
        games.forEach { registerGame(it) }
    }

    suspend fun getRegisteredGames(): List<Game> {
        val repoGames = repository.getGames()
        val combined = (repoGames + dynamicRegistry.values).distinctBy { it.id }
        return combined.filter { it.isEnabled }
    }

    suspend fun getGameById(id: String): Game? {
        return dynamicRegistry[id] ?: repository.getGame(id)
    }

    suspend fun findGameByPackageName(packageName: String): Game? {
        val games = getRegisteredGames()
        return games.find { game -> game.packageNames.contains(packageName) }
    }
}
