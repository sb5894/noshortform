package com.example.no_shortform

/** Evaluates one event only; false means no evidence, not a confirmed non-Shorts screen. */
internal object ShortsEvidenceDetector {
    // Observed on Shorts entry before the player-page event in the user's device log.
    // Match full IDs only: shared browsing/player controls are not Shorts evidence.
    fun hasEvidence(
        packageName: String?,
        viewIdResourceName: String?,
        mode: BlockingMode = BlockingMode.ENTRY
    ): Boolean =
        packageName == "com.google.android.youtube" &&
            (viewIdResourceName == "com.google.android.youtube:id/reel_player_page_container" ||
                (mode == BlockingMode.ENTRY &&
                    viewIdResourceName == "com.google.android.youtube:id/reel_recycler"))
}
