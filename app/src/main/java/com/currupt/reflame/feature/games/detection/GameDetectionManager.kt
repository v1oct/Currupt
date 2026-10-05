package com.currupt.reflame.feature.games.detection

import com.currupt.reflame.feature.games.GameRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameDetectionManager(
    private val detector: GameDetector = NoOpGameDetector(),
    private val gameRegistry: GameRegistry = GameRegistry()
) {
    private val _detectionResult = MutableStateFlow(
        GameDetectionResult(status = DetectionStatus.DETECTION_DISABLED)
    )
    val detectionResult: StateFlow<GameDetectionResult> = _detectionResult.asStateFlow()

    suspend fun performDetection(): GameDetectionResult {
        if (!detector.hasDetectionPermission()) {
            val result = GameDetectionResult(
                status = DetectionStatus.USAGE_STATS_PERMISSION_REQUIRED,
                errorMessage = "Usage Stats permission (PACKAGE_USAGE_STATS) is required for game detection."
            )
            _detectionResult.value = result
            return result
        }

        return try {
            val detectedPackage = detector.detectForegroundApplication()
            if (detectedPackage.isNullOrBlank()) {
                val result = GameDetectionResult(status = DetectionStatus.UNKNOWN_APPLICATION)
                _detectionResult.value = result
                return result
            }

            val matchedGame = gameRegistry.findGameByPackageName(detectedPackage)
            val result = when {
                matchedGame == null -> {
                    GameDetectionResult(
                        status = DetectionStatus.UNKNOWN_APPLICATION,
                        packageName = detectedPackage
                    )
                }
                !matchedGame.isV1Supported -> {
                    GameDetectionResult(
                        status = DetectionStatus.UNSUPPORTED_GAME_DETECTED,
                        packageName = detectedPackage,
                        game = matchedGame,
                        errorMessage = "'${matchedGame.displayName}' is not supported in V1 (Roblox only)."
                    )
                }
                else -> {
                    GameDetectionResult(
                        status = DetectionStatus.SUPPORTED_GAME_DETECTED,
                        packageName = detectedPackage,
                        game = matchedGame
                    )
                }
            }

            _detectionResult.value = result
            result
        } catch (e: Throwable) {
            val errorResult = GameDetectionResult(
                status = DetectionStatus.ERROR,
                errorMessage = e.message ?: "Unexpected error during game detection"
            )
            _detectionResult.value = errorResult
            errorResult
        }
    }
}
