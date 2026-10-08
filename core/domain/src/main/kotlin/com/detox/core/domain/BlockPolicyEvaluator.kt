package com.detox.core.domain

import com.detox.core.model.AppCategory
import com.detox.core.model.BlockActionType
import com.detox.core.model.BlockRule
import com.detox.core.model.BlockingConfig

sealed class BlockDecision {
    object Allowed : BlockDecision()
    data class Blocked(val rule: BlockRule, val actionType: BlockActionType) : BlockDecision()
}

interface BlockPolicyEvaluator {
    fun shouldBlock(
        targetPackage: String,
        category: AppCategory,
        quotaMet: Boolean,
        config: BlockingConfig
    ): BlockDecision
}

class DefaultBlockPolicyEvaluator : BlockPolicyEvaluator {

    override fun shouldBlock(
        targetPackage: String,
        category: AppCategory,
        quotaMet: Boolean,
        config: BlockingConfig
    ): BlockDecision {
        // Safe guard 1: Never block essential apps (dialer, settings, etc.)
        if (config.essentialApps.contains(targetPackage)) {
            return BlockDecision.Allowed
        }

        // Never block detox itself (safeguard)
        if (targetPackage.startsWith("com.detox")) {
            return BlockDecision.Allowed
        }

        for (rule in config.rules) {
            val targetsPackage = rule.targetPackages.contains(targetPackage)
            val targetsCategory = rule.targetCategories.contains(category)

            if (targetsPackage || targetsCategory) {
                if (rule.onlyWhenQuotaFailed) {
                    if (!quotaMet) {
                        return BlockDecision.Blocked(rule, rule.actionType)
                    }
                } else {
                    return BlockDecision.Blocked(rule, rule.actionType)
                }
            }
        }

        return BlockDecision.Allowed
    }
}
