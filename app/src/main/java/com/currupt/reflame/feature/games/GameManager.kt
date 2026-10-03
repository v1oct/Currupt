package com.currupt.reflame.feature.games

import com.currupt.reflame.core.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameManager(
    private val registry: GameRegistry = GameRegistry()
) {
    private val _state = MutableStateFlow(GameFeatureState(isLoading = true))
    val state: StateFlow<GameFeatureState> = _state.asStateFlow()

    suspend fun loadGames() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val games = registry.getRegisteredGames()
            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    availableGames = games,
                    selectedGame = currentState.selectedGame ?: games.firstOrNull()
                )
            }
        } catch (e: Throwable) {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load registered games"
                )
            }
        }
    }

    suspend fun selectGame(gameId: String): Boolean {
        val game = registry.getGameById(gameId)
        return if (game != null) {
            _state.update { it.copy(selectedGame = game) }
            true
        } else {
            false
        }
    }

    fun selectGame(game: Game) {
        _state.update { it.copy(selectedGame = game) }
    }
}
