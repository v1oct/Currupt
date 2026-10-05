package com.currupt.reflame.feature.games.detection

class NoOpGameDetector(
    private val simulatedPackage: String? = null,
    private val hasPermission: Boolean = true
) : GameDetector {

    override fun hasDetectionPermission(): Boolean = hasPermission

    override suspend fun detectForegroundApplication(): String? = simulatedPackage
}
