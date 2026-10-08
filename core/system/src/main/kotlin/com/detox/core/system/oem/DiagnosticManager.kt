package com.detox.core.system.oem

import android.content.Context
import android.os.PowerManager
import com.detox.core.system.collector.UsageCollector

data class DiagnosticStatus(
    val hasUsageStatsPermission: Boolean,
    val isBatteryOptimizationIgnored: Boolean,
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
            isXiaomiDevice = XiaomiHelper.isXiaomi
        )
    }
}
