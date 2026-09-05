package com.example.no_shortform

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShortsEvidenceDetectorTest {
    private val youtube = "com.google.android.youtube"
    private val candidate = "$youtube:id/reel_player_page_container"

    @Test
    fun exactCandidateFromYoutubeHasEvidence() {
        assertTrue(ShortsEvidenceDetector.hasEvidence(youtube, candidate))
    }

    @Test
    fun observedNonCandidateIdsHaveNoEvidence() {
        listOf(
            "browse_fragment_layout_coordinator_layout",
            "accessibility_layer_container",
            "fullscreen_button",
            "watch_player",
            "reel_progress_bar"
        ).forEach { id ->
            assertFalse(id, ShortsEvidenceDetector.hasEvidence(youtube, "$youtube:id/$id"))
        }
    }

    @Test
    fun missingOrDifferentPackageHasNoEvidence() {
        assertFalse(ShortsEvidenceDetector.hasEvidence(null, candidate))
        assertFalse(ShortsEvidenceDetector.hasEvidence("com.example.other", candidate))
    }

    @Test
    fun missingOrPartialIdHasNoEvidence() {
        listOf(null, "", "reel_player_page_container", "${candidate}_other", " $candidate")
            .forEach { id ->
                assertFalse(ShortsEvidenceDetector.hasEvidence(youtube, id))
            }
    }

    @Test
    fun evidenceDoesNotPersistIntoLaterEvents() {
        assertTrue(ShortsEvidenceDetector.hasEvidence(youtube, candidate))
        assertFalse(ShortsEvidenceDetector.hasEvidence(youtube, null))
        assertFalse(ShortsEvidenceDetector.hasEvidence(youtube, "$youtube:id/watch_player"))
        assertTrue(ShortsEvidenceDetector.hasEvidence(youtube, candidate))
    }
}
