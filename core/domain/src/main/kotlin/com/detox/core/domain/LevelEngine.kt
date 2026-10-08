package com.detox.core.domain

import com.detox.core.model.LevelConfig
import com.detox.core.model.LevelTier
import kotlin.math.pow
import kotlin.math.roundToLong

data class LevelProgress(
    val level: Int,
    val tier: LevelTier,
    val currentLevelXp: Long,
    val xpForNextLevel: Long,
    val progressRatio: Float // 0.0f to 1.0f
)

interface LevelEngine {
    fun calculateLevelProgress(totalXp: Long, config: LevelConfig): LevelProgress
    fun xpRequiredForLevel(level: Int, config: LevelConfig): Long
}

class DefaultLevelEngine : LevelEngine {

    override fun xpRequiredForLevel(level: Int, config: LevelConfig): Long {
        if (level <= 1) return 0L
        // Formula: xp(n) = base * (n - 1) ^ exponent
        return (config.baseRequiredXp * (level - 1.0).pow(config.exponent)).roundToLong()
    }

    override fun calculateLevelProgress(totalXp: Long, config: LevelConfig): LevelProgress {
        val safeXp = totalXp.coerceAtLeast(0L)
        var currentLevel = 1

        while (true) {
            val xpNeededForNext = xpRequiredForLevel(currentLevel + 1, config)
            if (safeXp < xpNeededForNext) {
                break
            }
            currentLevel++
        }

        val xpAtCurrentLevelStart = xpRequiredForLevel(currentLevel, config)
        val xpAtNextLevel = xpRequiredForLevel(currentLevel + 1, config)

        val xpInLevel = safeXp - xpAtCurrentLevelStart
        val totalXpNeededInLevel = (xpAtNextLevel - xpAtCurrentLevelStart).coerceAtLeast(1L)
        val progress = (xpInLevel.toFloat() / totalXpNeededInLevel.toFloat()).coerceIn(0.0f, 1.0f)

        val tier = config.tiers
            .filter { currentLevel >= it.fromLevel }
            .maxByOrNull { it.fromLevel }
            ?: config.tiers.first()

        return LevelProgress(
            level = currentLevel,
            tier = tier,
            currentLevelXp = xpInLevel,
            xpForNextLevel = totalXpNeededInLevel,
            progressRatio = progress
        )
    }
}
