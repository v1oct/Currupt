package com.currupt.reflame.core.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.client.model.OperationalModeConfig
import com.currupt.reflame.feature.games.detection.GameDetectionResult
import com.currupt.reflame.ui.theme.RΞTheme

class OverlayService : Service() {

    private val binder = LocalBinder()
    private var windowManager: WindowManager? = null

    private var edgeHandleView: EdgeLauncherOverlayView? = null
    private var panelComposeView: ComposeView? = null
    private var serviceLifecycleOwner: ServiceLifecycleOwner? = null

    private var isOverlayRunning = false
    private var isPanelVisible = false

    private var cachedGameResult: GameDetectionResult? = null
    private var cachedModeConfig: OperationalModeConfig? = null

    inner class LocalBinder : Binder() {
        fun getService(): OverlayService = this@OverlayService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        serviceLifecycleOwner = ServiceLifecycleOwner().apply {
            onCreate()
            onStart()
            onResume()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceNotification()
        showOverlayViews()
        return START_STICKY
    }

    private fun startForegroundServiceNotification() {
        val channelId = "currupt_overlay_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "CURRUPT Gaming Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("CURRUPT Gaming")
                .setContentText("Edge Launcher active")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("CURRUPT Gaming")
                .setContentText("Edge Launcher active")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        }

        startForeground(NOTIFICATION_ID, notification)
    }

    fun showOverlayViews() {
        if (isOverlayRunning) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) return

        try {
            val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            // 1. Inflate Edge Handle View
            val handleLp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                windowType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            }

            edgeHandleView = EdgeLauncherOverlayView(this) {
                togglePanelVisibility()
            }
            windowManager?.addView(edgeHandleView, handleLp)

            // 2. Compose View for CURRUPT Edge Panel
            val panelLp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                windowType,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
            }

            val lifecycleOwner = serviceLifecycleOwner ?: ServiceLifecycleOwner().also {
                it.onCreate()
                it.onStart()
                it.onResume()
                serviceLifecycleOwner = it
            }

            panelComposeView = ComposeView(this).apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeViewModelStoreOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)

                setContent {
                    RΞTheme(darkTheme = true) {
                        CurruptEdgePanelContent(
                            gameDetectionResult = cachedGameResult,
                            opModeConfig = cachedModeConfig,
                            userEntitlement = UserEntitlement.FREE,
                            onClosePanel = { setPanelVisible(false) }
                        )
                    }
                }
                visibility = View.GONE
            }

            windowManager?.addView(panelComposeView, panelLp)
            isOverlayRunning = true
            isPanelVisible = false
        } catch (_: Throwable) {
            isOverlayRunning = false
        }
    }

    fun hideOverlayViews() {
        try {
            edgeHandleView?.let { windowManager?.removeView(it) }
            panelComposeView?.let { windowManager?.removeView(it) }
        } catch (_: Throwable) {
        } finally {
            edgeHandleView = null
            panelComposeView = null
            isOverlayRunning = false
            isPanelVisible = false
        }
    }

    fun togglePanelVisibility() {
        setPanelVisible(!isPanelVisible)
    }

    fun setPanelVisible(visible: Boolean) {
        isPanelVisible = visible
        panelComposeView?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun isOverlayActive(): Boolean = isOverlayRunning

    fun isPanelVisible(): Boolean = isPanelVisible

    fun updateGameDetectionResult(result: GameDetectionResult) {
        cachedGameResult = result
    }

    fun updateOperationalModeConfig(config: OperationalModeConfig) {
        cachedModeConfig = config
    }

    override fun onDestroy() {
        serviceLifecycleOwner?.onDestroy()
        serviceLifecycleOwner = null
        hideOverlayViews()
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 2026
    }
}
