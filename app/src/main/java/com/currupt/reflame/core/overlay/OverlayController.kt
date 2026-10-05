package com.currupt.reflame.core.overlay

import com.currupt.reflame.client.model.OperationalModeConfig
import com.currupt.reflame.feature.games.detection.GameDetectionResult

interface OverlayController {
    fun startOverlay()
    fun stopOverlay()
    fun isOverlayActive(): Boolean
    fun togglePanelVisibility()
    fun setPanelVisible(visible: Boolean)
    fun isPanelVisible(): Boolean
    fun updateGameDetectionResult(result: GameDetectionResult)
    fun updateOperationalModeConfig(config: OperationalModeConfig)
}
