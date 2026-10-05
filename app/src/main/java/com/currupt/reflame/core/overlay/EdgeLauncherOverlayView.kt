package com.currupt.reflame.core.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView

class EdgeLauncherOverlayView(
    context: Context,
    private val onHandleClicked: () -> Unit
) : FrameLayout(context) {

    init {
        val handleBar = TextView(context).apply {
            text = "║"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER

            val backgroundDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#E50914"))
                cornerRadius = 16f
            }
            background = backgroundDrawable
            setPadding(12, 24, 12, 24)
        }

        val lp = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.END or Gravity.CENTER_VERTICAL
        )
        addView(handleBar, lp)

        setOnClickListener {
            onHandleClicked()
        }
    }
}
