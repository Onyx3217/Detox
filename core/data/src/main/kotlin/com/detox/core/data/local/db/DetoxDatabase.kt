package com.detox.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.detox.core.data.local.dao.DetoxDao
import com.detox.core.data.local.entity.AppSessionEntity
import com.detox.core.data.local.entity.DailyAppUsageEntity
import com.detox.core.data.local.entity.DailyStatsEntity
import com.detox.core.data.local.entity.UnlockEntity
import com.detox.core.data.local.entity.XpLedgerEntity

@Database(
    entities = [
        AppSessionEntity::class,
        UnlockEntity::class,
        DailyStatsEntity::class,
        DailyAppUsageEntity::class,
        XpLedgerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DetoxDatabase : RoomDatabase() {
    abstract fun detoxDao(): DetoxDao
}
