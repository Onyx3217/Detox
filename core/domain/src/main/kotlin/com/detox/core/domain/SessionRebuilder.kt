package com.detox.core.domain

import com.detox.core.model.AppSession

enum class RawEventType {
    RESUMED,
    PAUSED,
    SCREEN_OFF
}

data class RawUsageEvent(
    val packageName: String,
    val timestamp: Long,
    val type: RawEventType
)

class SessionRebuilder {

    /**
     * Reconstructs contiguous application usage sessions from raw UsageEvents.
     * Sessions are properly closed upon SCREEN_OFF or subsequent PAUSED events.
     */
    fun rebuildSessions(events: List<RawUsageEvent>): List<AppSession> {
        val openSessions = mutableMapOf<String, Long>()
        val completedSessions = mutableListOf<AppSession>()

        for (event in events.sortedBy { it.timestamp }) {
            when (event.type) {
                RawEventType.RESUMED -> {
                    openSessions[event.packageName] = event.timestamp
                }
                RawEventType.PAUSED -> {
                    val start = openSessions.remove(event.packageName)
                    if (start != null && event.timestamp > start) {
                        completedSessions.add(AppSession(event.packageName, start, event.timestamp))
                    }
                }
                RawEventType.SCREEN_OFF -> {
                    openSessions.forEach { (pkg, start) ->
                        if (event.timestamp > start) {
                            completedSessions.add(AppSession(pkg, start, event.timestamp))
                        }
                    }
                    openSessions.clear()
                }
            }
        }

        return completedSessions
    }
}
