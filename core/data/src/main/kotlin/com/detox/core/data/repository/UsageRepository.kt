package com.detox.core.data.repository

import com.detox.core.data.local.dao.DetoxDao
import com.detox.core.data.local.entity.AppSessionEntity
import com.detox.core.data.local.entity.DailyAppUsageEntity
import com.detox.core.data.local.entity.DailyStatsEntity
import com.detox.core.data.local.entity.UnlockEntity
import com.detox.core.data.local.entity.XpLedgerEntity
import kotlinx.coroutines.flow.Flow

class UsageRepository(
    private val dao: DetoxDao
) {
    fun getSessionsForDay(day: String): Flow<List<AppSessionEntity>> =
        dao.getSessionsForDay(day)

    suspend fun saveSessions(sessions: List<AppSessionEntity>) =
        dao.insertSessions(sessions)

    fun getUnlockCountForDay(day: String): Flow<Int> =
        dao.getUnlockCountForDay(day)

    suspend fun recordUnlock(timestampSec: Long, day: String) =
        dao.insertUnlock(UnlockEntity(ts = timestampSec, day = day))

    fun getDailyStats(day: String): Flow<DailyStatsEntity?> =
        dao.getDailyStats(day)

    suspend fun saveDailyStats(stats: DailyStatsEntity) =
        dao.upsertDailyStats(stats)

    fun getDailyAppUsage(day: String): Flow<List<DailyAppUsageEntity>> =
        dao.getDailyAppUsage(day)

    suspend fun saveDailyAppUsage(usages: List<DailyAppUsageEntity>) =
        dao.upsertDailyAppUsage(usages)

    fun getTotalXp(): Flow<Long> =
        dao.getTotalXp()

    suspend fun addXp(delta: Int, reason: String) =
        dao.insertXpEntry(XpLedgerEntity(ts = System.currentTimeMillis(), delta = delta, reason = reason))
}
