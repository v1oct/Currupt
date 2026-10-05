package com.currupt.reflame.core.overlay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.currupt.reflame.client.model.OperationalModeConfig
import com.currupt.reflame.feature.games.detection.GameDetectionResult

class AndroidOverlayController(
    private val context: Context
) : OverlayController {

    private var overlayService: OverlayService? = null
    private var isBound = false

    private var pendingGameResult: GameDetectionResult? = null
    private var pendingModeConfig: OperationalModeConfig? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? OverlayService.LocalBinder
            overlayService = binder?.getService()
            isBound = true

            pendingGameResult?.let { overlayService?.updateGameDetectionResult(it) }
            pendingModeConfig?.let { overlayService?.updateOperationalModeConfig(it) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            overlayService = null
            isBound = false
        }
    }

    override fun startOverlay() {
        val intent = Intent(context, OverlayService::class.java)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } catch (_: Throwable) {
        }
    }

    override fun stopOverlay() {
        try {
            if (isBound) {
                context.unbindService(connection)
                isBound = false
            }
            val intent = Intent(context, OverlayService::class.java)
            context.stopService(intent)
            overlayService = null
        } catch (_: Throwable) {
        }
    }

    override fun isOverlayActive(): Boolean = overlayService?.isOverlayActive() ?: false

    override fun togglePanelVisibility() {
        overlayService?.togglePanelVisibility()
    }

    override fun setPanelVisible(visible: Boolean) {
        overlayService?.setPanelVisible(visible)
    }

    override fun isPanelVisible(): Boolean = overlayService?.isPanelVisible() ?: false

    override fun updateGameDetectionResult(result: GameDetectionResult) {
        pendingGameResult = result
        overlayService?.updateGameDetectionResult(result)
    }

    override fun updateOperationalModeConfig(config: OperationalModeConfig) {
        pendingModeConfig = config
        overlayService?.updateOperationalModeConfig(config)
    }
}
