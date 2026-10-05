package com.currupt.reflame.core.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.currupt.reflame.client.model.OperationalMode
import com.currupt.reflame.client.model.OperationalModeConfig
import com.currupt.reflame.feature.games.detection.DetectionStatus
import com.currupt.reflame.feature.games.detection.GameDetectionResult

class ClientPanelPlaceholderView(
    context: Context,
    private val onCloseClicked: () -> Unit
) : FrameLayout(context) {

    private val titleView: TextView
    private val gameStateView: TextView
    private val modeStateView: TextView

    init {
        val panelContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)

            val backgroundDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#F0121212"))
                setStroke(3, Color.parseColor("#E50914"))
                cornerRadius = 24f
            }
            background = backgroundDrawable
        }

        titleView = TextView(context).apply {
            text = "CURRUPT GAMING CLIENT"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER_HORIZONTAL
        }
        panelContainer.addView(titleView)

        gameStateView = TextView(context).apply {
            text = "Current Game: None"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 16, 0, 8)
        }
        panelContainer.addView(gameStateView)

        modeStateView = TextView(context).apply {
            text = "Operational Mode: NORMAL"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 0, 0, 16)
        }
        panelContainer.addView(modeStateView)

        val closeButton = Button(context).apply {
            text = "CLOSE PANEL"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#333333"))
            setOnClickListener {
                onCloseClicked()
            }
        }
        panelContainer.addView(closeButton)

        val containerLp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        ).apply {
            setMargins(32, 32, 32, 32)
        }

        addView(panelContainer, containerLp)
    }

    fun updateGameDetectionResult(result: GameDetectionResult) {
        val gameText = when (result.status) {
            DetectionStatus.SUPPORTED_GAME_DETECTED -> {
                val gameName = result.game?.displayName ?: "Roblox"
                "Current Game: $gameName (V1 Supported)"
            }
            DetectionStatus.UNSUPPORTED_GAME_DETECTED -> {
                val gameName = result.game?.displayName ?: "Unsupported Game"
                "Current Game: $gameName (Unsupported in V1)"
            }
            DetectionStatus.UNKNOWN_APPLICATION -> "Current Game: None (Unknown App)"
            DetectionStatus.USAGE_STATS_PERMISSION_REQUIRED -> "Current Game: Permission Required"
            else -> "Current Game: None"
        }
        gameStateView.text = gameText
    }

    fun updateOperationalModeConfig(config: OperationalModeConfig) {
        val modeText = if (config.isEnabled && config.mode != OperationalMode.NORMAL) {
            "Operational Mode: ${config.mode.name} (${config.title.ifBlank { "Active" }})"
        } else {
            "Operational Mode: NORMAL"
        }
        modeStateView.text = modeText
    }
}
