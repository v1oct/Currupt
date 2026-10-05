package com.currupt.reflame.feature.games.detection

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidUsageStatsDetector(
    private val context: Context
) : GameDetector {

    override fun hasDetectionPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    @SuppressLint("WrongConstant")
    override suspend fun detectForegroundApplication(): String? = withContext(Dispatchers.IO) {
        if (!hasDetectionPermission()) return@withContext null

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return@withContext null

        val now = System.currentTimeMillis()
        val startTime = now - 10_000L

        val usageEvents = usageStatsManager.queryEvents(startTime, now) ?: return@withContext null
        val event = UsageEvents.Event()
        var currentForegroundPackage: String? = null

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                @Suppress("DEPRECATION") event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                currentForegroundPackage = event.packageName
            }
        }

        currentForegroundPackage
    }
}
