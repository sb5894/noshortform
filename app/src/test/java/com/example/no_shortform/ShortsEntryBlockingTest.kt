package com.example.no_shortform

import org.junit.Assert.assertEquals
import org.junit.Test

/** Replays metadata ordering; it does not simulate Android's back action or YouTube UI. */
class ShortsEntryBlockingTest {
    @Test
    fun firstEntryRequestsBackAtRecyclerThenSuppressesPlayerPage() {
        val policy = ShortsBlockingPolicy()
        val requestedAt = mutableListOf<Long>()
        // Relative timing from the first-entry log, with no user swipe.
        val events = listOf(
            10_000L to null,
            10_050L to "reel_recycler", // Content change
            10_050L to "reel_recycler", // Scrolled event emitted during entry
            10_150L to "browse_fragment_layout_coordinator_layout",
            10_253L to "reel_player_page_container",
            10_321L to null
        )
        events.forEach { (time, id) ->
            policy.tryBlock(
                enabled = true,
                shortsEvidence = ShortsEvidenceDetector.hasEvidence(
                    "com.google.android.youtube", id?.let { "com.google.android.youtube:id/$it" }
                ),
                eventTime = time,
                now = time,
                requestBack = { requestedAt.add(time); true }
            )
        }
        assertEquals(listOf(10_050L), requestedAt)
    }

    @Test
    fun entryEvidenceDoesNotBlockWhenSwitchIsOff() {
        var attempts = 0
        ShortsBlockingPolicy().tryBlock(
            enabled = false,
            shortsEvidence = ShortsEvidenceDetector.hasEvidence(
                "com.google.android.youtube", "com.google.android.youtube:id/reel_recycler"
            ),
            eventTime = 10_000,
            now = 10_000,
            requestBack = { attempts++; true }
        )
        assertEquals(0, attempts)
    }
}
