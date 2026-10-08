package com.detox.core.domain

import com.detox.core.model.DayInput
import com.detox.core.model.ScoreConfig
import com.detox.core.model.XpConfig
import kotlin.math.roundToInt

interface ScoreEngine {
    fun calculateScore(input: DayInput, config: ScoreConfig): Int
    fun calculateDailyXp(score: Int, streak: Int, config: XpConfig): Int
}

class DefaultScoreEngine : ScoreEngine {

    override fun calculateScore(input: DayInput, config: ScoreConfig): Int {
        var penalty = input.unlocks * config.unlockWeight

        for ((pkg, minutes) in input.minutesByApp) {
            val weight = config.appWeights[pkg] ?: config.defaultAppWeight
            penalty += (minutes * weight)
        }

        val rawScore = config.base - penalty + if (input.quotaMet) config.quotaBonus else 0.0
        return rawScore.roundToInt().coerceIn(0, 100)
    }

    override fun calculateDailyXp(score: Int, streak: Int, config: XpConfig): Int {
        if (score <= 0) return 0

        // Find applicable streak multiplier
        val multiplier = config.streakMultipliers
            .filter { streak >= it.days }
            .maxByOrNull { it.days }
            ?.multiplier ?: 1.0

        val earned = score * multiplier * config.baseDifficultyMultiplier
        return earned.roundToInt().coerceAtLeast(0)
    }
}
