package com.example.no_shortform

import org.junit.Assert.assertEquals
import org.junit.Test

class ShortsBlockingPolicyTest {
    private val policy = ShortsBlockingPolicy()
    private var attempts = 0

    private fun event(
        now: Long,
        enabled: Boolean = true,
        evidence: Boolean = true,
        eventTime: Long = now,
        success: Boolean = true
    ) {
        policy.tryBlock(enabled, evidence, eventTime, now) {
            attempts++
            success
        }
    }

    @Test
    fun disabledAndNonCandidateEventsDoNotRequestBack() {
        event(10_000, enabled = false)
        event(10_000, evidence = false)
        event(10_000, evidence = ShortsEvidenceDetector.hasEvidence(
            "com.example.other", "com.google.android.youtube:id/reel_player_page_container"
        ))
        assertEquals(0, attempts)
        event(10_000)
        assertEquals(1, attempts)
    }

    @Test
    fun staleFutureAndInvalidEventsDoNotRequestBack() {
        event(10_000, eventTime = 8_999)
        event(10_000, eventTime = 10_001)
        event(10_000, eventTime = -1)
        assertEquals(0, attempts)
        event(10_000, eventTime = 9_000)
        assertEquals(1, attempts)
    }

    @Test
    fun repeatedEventsAreSuppressedUntilExactCooldownBoundary() {
        event(10_000)
        event(10_000)
        event(11_999)
        assertEquals(1, attempts)
        event(12_000)
        assertEquals(2, attempts)
    }

    @Test
    fun falseEvidenceDoesNotResetCooldown() {
        event(10_000)
        event(10_100, evidence = false)
        event(10_200)
        assertEquals(1, attempts)
    }

    @Test
    fun switchingOffAndOnDoesNotResetCooldown() {
        event(10_000)
        event(10_100, enabled = false)
        event(10_200, enabled = true)
        assertEquals(1, attempts)
        event(12_000)
        assertEquals(2, attempts)
    }

    @Test
    fun failedRequestStillConsumesCooldown() {
        event(10_000, success = false)
        event(10_100)
        event(11_999)
        assertEquals(1, attempts)
        event(12_000)
        assertEquals(2, attempts)
    }

    @Test
    fun cooldownIsReservedBeforeActionCallback() {
        policy.tryBlock(true, true, 10_000, 10_000) {
            attempts++
            event(10_000)
            true
        }
        assertEquals(1, attempts)
    }

    @Test
    fun newServicePolicyStartsWithoutPreviousCooldown() {
        event(10_000)
        ShortsBlockingPolicy().tryBlock(true, true, 10_100, 10_100) {
            attempts++
            true
        }
        assertEquals(2, attempts)
    }
}
