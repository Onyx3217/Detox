package com.detox.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.detox.core.data.local.entity.AppSessionEntity
import com.detox.core.data.local.entity.DailyAppUsageEntity
import com.detox.core.data.local.entity.DailyStatsEntity
import com.detox.core.data.local.entity.UnlockEntity
import com.detox.core.data.local.entity.XpLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetoxDao {

    // App Sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<AppSessionEntity>)

    @Query("SELECT * FROM app_session WHERE day = :day ORDER BY startTs ASC")
    fun getSessionsForDay(day: String): Flow<List<AppSessionEntity>>

    // Unlocks
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnlock(unlock: UnlockEntity)

    @Query("SELECT COUNT(*) FROM unlock_event WHERE day = :day")
    fun getUnlockCountForDay(day: String): Flow<Int>

    // Daily Stats
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyStats(stats: DailyStatsEntity)

    @Query("SELECT * FROM daily_stats WHERE day = :day LIMIT 1")
    fun getDailyStats(day: String): Flow<DailyStatsEntity?>

    @Query("SELECT * FROM daily_stats ORDER BY day DESC LIMIT 30")
    fun getLast30DaysStats(): Flow<List<DailyStatsEntity>>

    // Daily App Usage
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyAppUsage(usages: List<DailyAppUsageEntity>)

    @Query("SELECT * FROM daily_app_usage WHERE day = :day ORDER BY timeMs DESC")
    fun getDailyAppUsage(day: String): Flow<List<DailyAppUsageEntity>>

    // XP Ledger
    @Insert
    suspend fun insertXpEntry(entry: XpLedgerEntity)

    @Query("SELECT COALESCE(SUM(delta), 0) FROM xp_ledger")
    fun getTotalXp(): Flow<Long>
}
