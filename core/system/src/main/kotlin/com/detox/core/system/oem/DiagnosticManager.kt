package com.detox.core.system.oem

import android.content.Context
import android.os.PowerManager
import com.detox.core.system.collector.UsageCollector

data class DiagnosticStatus(
    val hasUsageStatsPermission: Boolean,
    val isBatteryOptimizationIgnored: Boolean,
    val isAccessibilityEnabled: Boolean,
    val isXiaomiDevice: Boolean
)

class DiagnosticManager(
    private val context: Context
) {
    private val usageCollector = UsageCollector(context)

    fun checkStatus(): DiagnosticStatus {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isBatteryIgnored = powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false

        return DiagnosticStatus(
            hasUsageStatsPermission = usageCollector.hasPermission(),
            isBatteryOptimizationIgnored = isBatteryIgnored,
            isAccessibilityEnabled = isAccessibilityServiceEnabled(),
            isXiaomiDevice = XiaomiHelper.isXiaomi
        )
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedServiceName = "${context.packageName}/com.detox.core.system.service.BlockerAccessibilityService"
        val enabledServices = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val colonSplitter = enabledServices.split(":")
        for (service in colonSplitter) {
            if (service.equals(expectedServiceName, ignoreCase = true) ||
                service.contains("BlockerAccessibilityService", ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}
