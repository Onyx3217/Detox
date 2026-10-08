package com.detox.core.system.collector

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.detox.core.domain.RawEventType
import com.detox.core.domain.RawUsageEvent

class UsageCollector(
    private val context: Context
) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

    /**
     * Checks if PACKAGE_USAGE_STATS permission has been granted by user in system settings.
     */
    fun hasPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? android.app.AppOpsManager ?: return false
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    /**
     * Queries UsageEvents between beginTimestamp and endTimestamp,
     * converting raw Android events into domain RawUsageEvent.
     */
    fun queryEvents(beginTimestamp: Long, endTimestamp: Long): List<RawUsageEvent> {
        val manager = usageStatsManager ?: return emptyList()
        val usageEvents = manager.queryEvents(beginTimestamp, endTimestamp)
        val result = mutableListOf<RawUsageEvent>()
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            val rawType = when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> RawEventType.RESUMED
                UsageEvents.Event.ACTIVITY_PAUSED, UsageEvents.Event.ACTIVITY_STOPPED -> RawEventType.PAUSED
                UsageEvents.Event.SCREEN_NON_INTERACTIVE -> RawEventType.SCREEN_OFF
                else -> null
            }

            if (rawType != null) {
                result.add(
                    RawUsageEvent(
                        packageName = event.packageName ?: "",
                        timestamp = event.timeStamp,
                        type = rawType
                    )
                )
            }
        }

        return result
    }

    /**
     * Extracts unlock events from UsageEvents (KEYGUARD_HIDDEN) for reliable retroactive counting.
     */
    fun queryRetroactiveUnlocks(beginTimestamp: Long, endTimestamp: Long): List<Long> {
        val manager = usageStatsManager ?: return emptyList()
        val usageEvents = manager.queryEvents(beginTimestamp, endTimestamp)
        val unlocks = mutableListOf<Long>()
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                // Round to second to deduplicate with real-time broadcast
                unlocks.add(event.timeStamp / 1000L)
            }
        }

        return unlocks.distinct()
    }
}
