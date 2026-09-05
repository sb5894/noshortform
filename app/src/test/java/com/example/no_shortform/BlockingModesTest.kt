package com.example.no_shortform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BlockingModesTest {
    private val youtube = "com.google.android.youtube"

    @Test
    fun missingOrUnknownStoredModeDefaultsToEntry() {
        listOf(null, "", "unknown", "legacy").forEach {
            assertEquals(BlockingMode.ENTRY, BlockingMode.fromStoredValue(it))
        }
        BlockingMode.entries.forEach {
            assertEquals(it, BlockingMode.fromStoredValue(it.name))
        }
    }

    @Test
    fun playerPageMatchesBothModesButRecyclerOnlyMatchesEntry() {
        BlockingMode.entries.forEach { mode ->
            assertTrue(ShortsEvidenceDetector.hasEvidence(youtube, "$youtube:id/reel_player_page_container", mode))
            assertEquals(mode == BlockingMode.ENTRY,
                ShortsEvidenceDetector.hasEvidence(youtube, "$youtube:id/reel_recycler", mode))
        }
    }

    @Test
    fun unrelatedAndIncompleteMetadataNeverMatchesEitherMode() {
        BlockingMode.entries.forEach { mode ->
            listOf(null, "", "reel_recycler", "$youtube:id/reel_recycler_preview",
                "$youtube:id/reel_progress_bar", "$youtube:id/watch_player",
                "$youtube:id/browse_fragment_layout_coordinator_layout",
                "$youtube:id/accessibility_layer_container", "$youtube:id/fullscreen_button"
            ).forEach { id ->
                assertFalse(ShortsEvidenceDetector.hasEvidence(youtube, id, mode))
            }
            listOf(null, "com.example.other").forEach { pkg ->
                listOf("reel_player_page_container", "reel_recycler").forEach { id ->
                    assertFalse(ShortsEvidenceDetector.hasEvidence(pkg, "$youtube:id/$id", mode))
                }
            }
        }
    }

    @Test
    fun observedEntrySequenceUsesModeSpecificFirstCandidate() {
        BlockingMode.entries.forEach { mode ->
            val policy = ShortsBlockingPolicy()
            val attempts = mutableListOf<Long>()
            listOf(
                10_000L to null,
                10_050L to "reel_recycler",
                10_050L to "reel_recycler",
                10_150L to "browse_fragment_layout_coordinator_layout",
                10_253L to "reel_player_page_container",
                10_321L to null
            ).forEach { (time, id) ->
                policy.tryBlock(true, ShortsEvidenceDetector.hasEvidence(
                    youtube, id?.let { "$youtube:id/$it" }, mode
                ), time, time) { attempts.add(time); true }
            }
            // Legacy is an ID rule, not a guarantee of allowing the first video.
            assertEquals(listOf(if (mode == BlockingMode.ENTRY) 10_050L else 10_253L), attempts)
        }
    }

    @Test
    fun disabledSwitchPreventsBackInBothModes() {
        var attempts = 0
        BlockingMode.entries.forEach { mode ->
            listOf("reel_recycler", "reel_player_page_container").forEach { id ->
                ShortsBlockingPolicy().tryBlock(false, ShortsEvidenceDetector.hasEvidence(
                    youtube, "$youtube:id/$id", mode
                ), 10_000, 10_000) { attempts++; true }
            }
        }
        assertEquals(0, attempts)
    }

    @Test
    fun modeChangesApplyWithoutRecreatingPolicyOrResettingCooldown() {
        val policy = ShortsBlockingPolicy()
        var attempts = 0
        fun event(mode: BlockingMode, time: Long, enabled: Boolean = true) {
            policy.tryBlock(enabled, ShortsEvidenceDetector.hasEvidence(
                youtube, "$youtube:id/reel_recycler", mode
            ), time, time) { attempts++; true }
        }
        event(BlockingMode.LEGACY, 10_000)
        assertEquals(0, attempts)
        event(BlockingMode.ENTRY, 10_100)
        assertEquals(1, attempts)
        event(BlockingMode.LEGACY, 10_200, enabled = false)
        event(BlockingMode.ENTRY, 10_300)
        event(BlockingMode.ENTRY, 12_099)
        assertEquals(1, attempts)
        event(BlockingMode.ENTRY, 12_100)
        assertEquals(2, attempts)
    }
}
