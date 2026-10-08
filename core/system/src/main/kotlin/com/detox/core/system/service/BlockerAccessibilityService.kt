package com.detox.core.system.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class BlockerAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        // Ignore System UI, launcher, keyboards, and Detox itself
        if (isIgnoredPackage(packageName)) {
            return
        }

        onAppWindowChanged(packageName)
    }

    private fun onAppWindowChanged(packageName: String) {
        // Evaluates blocking rules here
        // If blocked: trigger home intent + open BlockActivity overlay
    }

    private fun isIgnoredPackage(packageName: String): Boolean {
        return packageName.startsWith("com.detox") ||
                packageName == "com.android.systemui" ||
                packageName == "com.google.android.inputmethod.latin" ||
                packageName.contains("launcher")
    }

    override fun onInterrupt() {
        // Required callback when accessibility service interrupted
    }
}
