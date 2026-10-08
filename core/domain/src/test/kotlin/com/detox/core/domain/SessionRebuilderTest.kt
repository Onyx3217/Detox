package com.detox.core.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class SessionRebuilderTest {

    private val rebuilder = SessionRebuilder()

    @Test
    fun `reconstructs clean sessions and closes on screen off`() {
        val events = listOf(
            RawUsageEvent("com.instagram.android", 1000L, RawEventType.RESUMED),
            RawUsageEvent("com.instagram.android", 5000L, RawEventType.PAUSED),
            RawUsageEvent("com.whatsapp", 6000L, RawEventType.RESUMED),
            RawUsageEvent("", 12000L, RawEventType.SCREEN_OFF)
        )

        val sessions = rebuilder.rebuildSessions(events)

        assertEquals(2, sessions.size)
        assertEquals("com.instagram.android", sessions[0].packageName)
        assertEquals(4000L, sessions[0].durationMs)

        assertEquals("com.whatsapp", sessions[1].packageName)
        assertEquals(6000L, sessions[1].durationMs)
    }
}
