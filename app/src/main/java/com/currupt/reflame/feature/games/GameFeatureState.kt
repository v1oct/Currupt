package com.currupt.reflame.feature.games

import com.currupt.reflame.core.model.Game

data class GameFeatureState(
    val isLoading: Boolean = false,
    val availableGames: List<Game> = emptyList(),
    val selectedGame: Game? = null,
    val errorMessage: String? = null
)
