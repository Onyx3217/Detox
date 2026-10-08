package com.detox.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class AppCategory {
    SOCIAL,
    GAMES,
    VIDEO,
    STREAMING,
    MESSAGING,
    PRODUCTIVITY,
    UTILITY,
    HEALTH_WELLNESS,
    OTHER
}

@Serializable
data class AppSession(
    val packageName: String,
    val startTimestamp: Long,
    val endTimestamp: Long
) {
    val durationMs: Long
        get() = (endTimestamp - startTimestamp).coerceAtLeast(0L)

    val durationMinutes: Double
        get() = durationMs / 60_000.0
}

@Serializable
data class DayInput(
    val day: String, // "YYYY-MM-DD"
    val unlocks: Int,
    val minutesByApp: Map<String, Double>,
    val quotaMet: Boolean = false
) {
    val totalScreenMinutes: Double
        get() = minutesByApp.values.sum()
}
