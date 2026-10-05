package com.currupt.reflame.feature.games.detection

interface GameDetector {
    suspend fun detectForegroundApplication(): String?
    fun hasDetectionPermission(): Boolean
}
