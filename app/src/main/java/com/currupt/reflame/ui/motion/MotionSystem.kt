package com.currupt.reflame.ui.motion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * CURRUPT. Studio Motion System
 * 
 * Reusable animations for entrance, scroll reveal, and page transitions.
 */
object MotionSystem {

    // Common Durations
    const val DurationShort = 300
    const val DurationMedium = 500
    const val DurationLong = 800
    const val DurationExtraLong = 1200

    // Common Easings
    val StandardEasing = FastOutSlowInEasing
    val DecelerateEasing = LinearOutSlowInEasing

    /**
     * Subtle entrance animation (Fade + Slide)
     */
    @Composable
    fun EntranceTransition(
        delayMillis: Int = 0,
        content: @Composable () -> Unit
    ) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            visible = true
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(DurationMedium, delayMillis, StandardEasing)
            ) + slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = tween(DurationMedium, delayMillis, StandardEasing)
            ),
            exit = fadeOut(animationSpec = tween(DurationShort))
        ) {
            content()
        }
    }

    /**
     * Scroll-aware reveal (Scale + Fade + Translate)
     */
    @Composable
    fun ScrollReveal(
        index: Int = 0,
        content: @Composable () -> Unit
    ) {
        val delay = (index % 10) * 100 // Staggering
        var visible by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            visible = true
        }

        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(DurationMedium, delay, StandardEasing),
            label = "alpha"
        )
        
        val scale by animateFloatAsState(
            targetValue = if (visible) 1f else 0.97f,
            animationSpec = tween(DurationMedium, delay, StandardEasing),
            label = "scale"
        )

        val translateY by animateFloatAsState(
            targetValue = if (visible) 0f else 20f,
            animationSpec = tween(DurationMedium, delay, StandardEasing),
            label = "translateY"
        )

        Box(
            modifier = Modifier.graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
                this.translationY = translateY
            }
        ) {
            content()
        }
    }
}
