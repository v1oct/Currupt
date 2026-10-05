package com.currupt.reflame.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.currupt.reflame.ui.design.CurruptColors

data class BackgroundConfig(
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val opacity: Float = 0.3f,
    val blurIntensity: Float = 10f,
    val showAmbientGlow: Boolean = true
)

@Composable
fun CurruptBackground(
    modifier: Modifier = Modifier,
    config: BackgroundConfig = BackgroundConfig(),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CurruptColors.BackgroundBlack)
    ) {
        // Base Ambient Gradient Layer
        if (config.showAmbientGlow) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E1E28).copy(alpha = config.opacity),
                                CurruptColors.BackgroundBlack
                            )
                        )
                    )
            )
        }

        // Dark Scrim Overlay to guarantee text legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black
                        )
                    )
                )
        )

        // Content Layer Above Background
        content()
    }
}
