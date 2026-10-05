package com.currupt.reflame.client.runtime

import com.currupt.reflame.client.model.OperationalMode
import com.currupt.reflame.client.model.OperationalModeConfig
import com.currupt.reflame.core.config.ConfigManager
import com.currupt.reflame.core.overlay.OverlayController
import com.currupt.reflame.feature.games.detection.GameDetectionManager
import com.currupt.reflame.feature.games.detection.GameDetectionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClientRuntimeManager(
    private val permissionChecker: OverlayPermissionChecker,
    private val overlayController: OverlayController,
    private val configManager: ConfigManager = ConfigManager(),
    private val gameDetectionManager: GameDetectionManager = GameDetectionManager()
) {
    private val _runtimeState = MutableStateFlow(ClientRuntimeState())
    val runtimeState: StateFlow<ClientRuntimeState> = _runtimeState.asStateFlow()

    private val _permissionStatus = MutableStateFlow(
        if (permissionChecker.hasOverlayPermission()) PermissionStatus.GRANTED else PermissionStatus.REQUIRED
    )
    val permissionStatus: StateFlow<PermissionStatus> = _permissionStatus.asStateFlow()

    val gameDetectionResult: StateFlow<GameDetectionResult> = gameDetectionManager.detectionResult
    val operationalModeConfig: StateFlow<OperationalModeConfig> = configManager.operationalModeConfig

    fun checkPermissionStatus(): PermissionStatus {
        val granted = permissionChecker.hasOverlayPermission()
        val status = if (granted) PermissionStatus.GRANTED else PermissionStatus.REQUIRED
        _permissionStatus.value = status
        return status
    }

    fun openOverlayPermissionSettings() {
        permissionChecker.openOverlayPermissionSettings()
    }

    suspend fun startRuntime(): Boolean {
        if (!permissionChecker.hasOverlayPermission()) {
            _permissionStatus.value = PermissionStatus.REQUIRED
            _runtimeState.value = ClientRuntimeState(
                status = ClientRuntimeStatus.ERROR,
                permissionStatus = PermissionStatus.REQUIRED,
                errorMessage = "Overlay permission (SYSTEM_ALERT_WINDOW) is required to start CURRUPT Gaming Edge Panel."
            )
            return false
        }

        _permissionStatus.value = PermissionStatus.GRANTED

        val opModeConfig = configManager.operationalModeConfig.value
        if (opModeConfig.isEnabled && opModeConfig.mode == OperationalMode.EMERGENCY) {
            _runtimeState.value = ClientRuntimeState(
                status = ClientRuntimeStatus.ERROR,
                permissionStatus = PermissionStatus.GRANTED,
                errorMessage = "Client runtime is disabled due to EMERGENCY Operational Mode."
            )
            return false
        }

        val currentStatus = _runtimeState.value.status
        if (currentStatus == ClientRuntimeStatus.RUNNING || currentStatus == ClientRuntimeStatus.STARTING) {
            return true
        }

        _runtimeState.value = _runtimeState.value.copy(
            status = ClientRuntimeStatus.STARTING,
            errorMessage = null
        )

        return try {
            overlayController.startOverlay()
            _runtimeState.value = _runtimeState.value.copy(
                status = ClientRuntimeStatus.RUNNING
            )

            overlayController.updateOperationalModeConfig(opModeConfig)

            val detectionResult = gameDetectionManager.performDetection()
            overlayController.updateGameDetectionResult(detectionResult)

            true
        } catch (e: Throwable) {
            _runtimeState.value = _runtimeState.value.copy(
                status = ClientRuntimeStatus.ERROR,
                errorMessage = e.message ?: "Failed to start overlay runtime service."
            )
            false
        }
    }

    fun stopRuntime() {
        val currentStatus = _runtimeState.value.status
        if (currentStatus == ClientRuntimeStatus.NOT_STARTED || currentStatus == ClientRuntimeStatus.STOPPING) {
            return
        }

        _runtimeState.value = _runtimeState.value.copy(status = ClientRuntimeStatus.STOPPING)

        try {
            overlayController.stopOverlay()
        } catch (_: Throwable) {
        } finally {
            _runtimeState.value = ClientRuntimeState(
                status = ClientRuntimeStatus.NOT_STARTED,
                permissionStatus = _permissionStatus.value
            )
        }
    }

    fun togglePanel() {
        if (_runtimeState.value.status == ClientRuntimeStatus.RUNNING) {
            overlayController.togglePanelVisibility()
        }
    }
}
