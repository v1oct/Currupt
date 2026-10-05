package com.currupt.reflame.feature.games

import com.currupt.reflame.core.model.GameProfile
import kotlinx.serialization.Serializable

@Serializable
sealed interface GameProfileValidationResult {
    data object Valid : GameProfileValidationResult
    data class Invalid(val reason: String) : GameProfileValidationResult
}

object GameProfileValidator {
    fun validate(profile: GameProfile): GameProfileValidationResult {
        if (profile.gameId.isBlank()) {
            return GameProfileValidationResult.Invalid("Game ID cannot be blank.")
        }
        if (profile.profileVersion < 1L) {
            return GameProfileValidationResult.Invalid("Profile version must be >= 1.")
        }
        return GameProfileValidationResult.Valid
    }
}
