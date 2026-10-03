package com.currupt.reflame.feature.performance

data class PerformanceFeatureState(
    val isMonitoringActive: Boolean = false,
    val currentFps: Int = 0,
    val memoryUsageMb: Long = 0L
)
