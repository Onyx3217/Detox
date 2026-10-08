package com.detox.core.domain

import com.detox.core.model.DayInput
import com.detox.core.model.ScoreConfig
import com.detox.core.model.StreakMultiplier
import com.detox.core.model.XpConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreEngineTest {

    private val engine = DefaultScoreEngine()

    @Test
    fun `perfect day with zero screen time and zero unlocks achieves maximum score`() {
        val input = DayInput(
            day = "2026-10-08",
            unlocks = 0,
            minutesByApp = emptyMap(),
            quotaMet = true
        )
        val config = ScoreConfig(base = 100.0, quotaBonus = 10.0)
        val score = engine.calculateScore(input, config)

        // Clamped at 100 max
        assertEquals(100, score)
    }

    @Test
    fun `excessive usage and unlocks cannot drop score below zero`() {
        val input = DayInput(
            day = "2026-10-08",
            unlocks = 300, // 300 * 0.5 = 150 penalty
            minutesByApp = mapOf("com.instagram.android" to 600.0), // 600 * 0.3 = 180 penalty
            quotaMet = false
        )
        val config = ScoreConfig()
        val score = engine.calculateScore(input, config)

        assertEquals(0, score)
    }

    @Test
    fun `streak multiplier awards progressive XP correctly`() {
        val config = XpConfig(
            streakMultipliers = listOf(
                StreakMultiplier(days = 3, multiplier = 1.2),
                StreakMultiplier(days = 7, multiplier = 1.5)
            )
        )

        val xpDay1 = engine.calculateDailyXp(score = 80, streak = 1, config = config)
        val xpDay3 = engine.calculateDailyXp(score = 80, streak = 3, config = config)
        val xpDay7 = engine.calculateDailyXp(score = 80, streak = 10, config = config)

        assertEquals(80, xpDay1)
        assertEquals(96, xpDay3) // 80 * 1.2
        assertEquals(120, xpDay7) // 80 * 1.5
    }
}
