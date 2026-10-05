package com.currupt.reflame.feature.games.detection

import com.currupt.reflame.core.model.Game

enum class DetectionStatus {
    SUPPORTED_GAME_DETECTED,
    UNSUPPORTED_GAME_DETECTED,
    UNKNOWN_APPLICATION,
    USAGE_STATS_PERMISSION_REQUIRED,
    DETECTION_DISABLED,
    ERROR
}

data class GameDetectionResult(
    val status: DetectionStatus,
    val packageName: String? = null,
    val game: Game? = null,
    val errorMessage: String? = null
)
