package com.detox.core.system.oem

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

object XiaomiHelper {

    /**
     * Checks if current device runs MIUI or HyperOS.
     */
    val isXiaomi: Boolean
        get() = Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) ||
                Build.BRAND.equals("Xiaomi", ignoreCase = true) ||
                Build.BRAND.equals("Redmi", ignoreCase = true) ||
                Build.BRAND.equals("POCO", ignoreCase = true)

    /**
     * Intent for Autostart management on MIUI / HyperOS.
     */
    fun getAutostartIntent(context: Context): Intent {
        val intents = listOf(
            Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            ),
            Intent().setComponent(
                ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
            )
        )

        for (intent in intents) {
            if (isIntentCallable(context, intent)) {
                return intent
            }
        }

        return getAppDetailsIntent(context)
    }

    /**
     * Intent for MIUI Battery Saver settings (No restrictions / Pas de restrictions).
     */
    fun getBatteryOptimizationIntent(context: Context): Intent {
        val intent = Intent().setComponent(
            ComponentName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
            )
        ).apply {
            putExtra("package_name", context.packageName)
            putExtra("package_label", "Detox")
        }

        return if (isIntentCallable(context, intent)) {
            intent
        } else {
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        }
    }

    /**
     * Intent to open App Info (critical on Android 13+ to allow 'Restricted Settings'
     * for accessibility service when installed via APK/sideload).
     */
    fun getAppDetailsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun isIntentCallable(context: Context, intent: Intent): Boolean {
        return context.packageManager.queryIntentActivities(intent, 0).isNotEmpty()
    }
}
