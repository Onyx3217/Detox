package com.detox.core.model

import kotlinx.serialization.Serializable

@Serializable
data class DetoxConfig(
    val schemaVersion: Int = 1,
    val meta: ConfigMeta = ConfigMeta(),
    val score: ScoreConfig = ScoreConfig(),
    val quota: QuotaConfig = QuotaConfig(),
    val xp: XpConfig = XpConfig(),
    val levels: LevelConfig = LevelConfig(),
    val blocking: BlockingConfig = BlockingConfig()
)

@Serializable
data class ConfigMeta(
    val name: String = "Default Detox",
    val author: String = "User"
)

@Serializable
data class ScoreConfig(
    val base: Double = 100.0,
    val unlockWeight: Double = 0.5,
    val quotaBonus: Double = 10.0,
    val defaultAppWeight: Double = 0.10,
    val appWeights: Map<String, Double> = mapOf(
        "com.instagram.android" to 0.30,
        "com.zhiliaoapp.musically" to 0.30,
        "com.google.android.youtube" to 0.15,
        "com.whatsapp" to 0.05
    ),
    val categoryWeights: Map<AppCategory, Double> = mapOf(
        AppCategory.SOCIAL to 0.30,
        AppCategory.GAMES to 0.20,
        AppCategory.VIDEO to 0.15,
        AppCategory.MESSAGING to 0.05,
        AppCategory.UTILITY to 0.00,
        AppCategory.PRODUCTIVITY to 0.00
    )
)

@Serializable
enum class QuotaMode {
    ALL,
    ANY
}

@Serializable
enum class QuotaConditionType {
    MAX_UNLOCKS,
    MAX_SCREEN_TIME_MIN,
    MAX_APP_TIME_MIN,
    NIGHT_QUIET
}

@Serializable
data class QuotaCondition(
    val type: QuotaConditionType,
    val targetValue: Double = 0.0,
    val packageName: String? = null,
    val timeFrom: String? = null, // "23:00"
    val timeTo: String? = null    // "07:00"
)

@Serializable
data class QuotaConfig(
    val mode: QuotaMode = QuotaMode.ALL,
    val conditions: List<QuotaCondition> = listOf(
        QuotaCondition(type = QuotaConditionType.MAX_UNLOCKS, targetValue = 50.0),
        QuotaCondition(type = QuotaConditionType.MAX_SCREEN_TIME_MIN, targetValue = 180.0)
    ),
    val dayStartsAt: String = "00:00"
)

@Serializable
data class StreakMultiplier(
    val days: Int,
    val multiplier: Double
)

@Serializable
data class XpConfig(
    val streakMultipliers: List<StreakMultiplier> = listOf(
        StreakMultiplier(days = 3, multiplier = 1.2),
        StreakMultiplier(days = 7, multiplier = 1.5),
        StreakMultiplier(days = 30, multiplier = 2.0)
    ),
    val emergencyCost: Int = 50,
    val baseDifficultyMultiplier: Double = 1.0
)

@Serializable
data class LevelTier(
    val fromLevel: Int,
    val name: String,
    val theme: String
)

@Serializable
data class LevelConfig(
    val baseRequiredXp: Double = 100.0,
    val exponent: Double = 1.5,
    val tiers: List<LevelTier> = listOf(
        LevelTier(fromLevel = 1, name = "Braise", theme = "ember"),
        LevelTier(fromLevel = 5, name = "Flamme", theme = "flame"),
        LevelTier(fromLevel = 10, name = "Aurore", theme = "aurora"),
        LevelTier(fromLevel = 20, name = "Nébuleuse", theme = "nebula"),
        LevelTier(fromLevel = 35, name = "Zénith", theme = "zenith")
    )
)

@Serializable
enum class BlockActionType {
    BLOCK,
    WARN,
    BREATHE_PAUSE
}

@Serializable
data class BlockRule(
    val id: String,
    val name: String,
    val targetPackages: List<String> = emptyList(),
    val targetCategories: List<AppCategory> = emptyList(),
    val actionType: BlockActionType = BlockActionType.BLOCK,
    val coolDownSec: Int = 15,
    val onlyWhenQuotaFailed: Boolean = true
)

@Serializable
data class BlockingConfig(
    val essentialApps: List<String> = listOf(
        "com.android.dialer",
        "com.android.settings",
        "com.google.android.dialer",
        "com.android.emergency"
    ),
    val rules: List<BlockRule> = emptyList()
)
