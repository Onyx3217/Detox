package com.detox.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_session")
data class AppSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val startTs: Long,
    val endTs: Long,
    val day: String // "YYYY-MM-DD"
)

@Entity(tableName = "unlock_event")
data class UnlockEntity(
    @PrimaryKey val ts: Long, // timestamp en secondes pour déduplication
    val day: String
)

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey val day: String,
    val unlocks: Int,
    val screenTimeMs: Long,
    val score: Int,
    val xpGained: Int,
    val quotaMet: Boolean,
    val finalized: Boolean = false
)

@Entity(tableName = "daily_app_usage", primaryKeys = ["day", "packageName"])
data class DailyAppUsageEntity(
    val day: String,
    val packageName: String,
    val timeMs: Long,
    val launches: Int
)

@Entity(tableName = "xp_ledger")
data class XpLedgerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ts: Long,
    val delta: Int, // Gain ou perte d'XP
    val reason: String // DAILY | STREAK_BONUS | EMERGENCY_COST
)
