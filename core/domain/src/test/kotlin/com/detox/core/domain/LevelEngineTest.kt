package com.detox.core.domain

import com.detox.core.model.LevelConfig
import com.detox.core.model.LevelTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelEngineTest {

    private val engine = DefaultLevelEngine()
    private val config = LevelConfig(
        baseRequiredXp = 100.0,
        exponent = 1.5,
        tiers = listOf(
            LevelTier(fromLevel = 1, name = "Braise", theme = "ember"),
            LevelTier(fromLevel = 5, name = "Flamme", theme = "flame"),
            LevelTier(fromLevel = 10, name = "Aurore", theme = "aurora")
        )
    )

    @Test
    fun `level 1 starts at zero xp`() {
        val progress = engine.calculateLevelProgress(0L, config)
        assertEquals(1, progress.level)
        assertEquals("Braise", progress.tier.name)
        assertEquals(0L, progress.currentLevelXp)
        assertTrue(progress.progressRatio >= 0.0f)
    }

    @Test
    fun `higher XP unlocks higher tiers and level`() {
        // level 5 requires 100 * (4)^1.5 = 100 * 8 = 800 XP
        val progress = engine.calculateLevelProgress(850L, config)
        assertEquals(5, progress.level)
        assertEquals("Flamme", progress.tier.name)
    }
}
