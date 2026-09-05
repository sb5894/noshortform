package com.example.no_shortform

/** Evaluates one event only; false means no evidence, not a confirmed non-Shorts screen. */
internal object ShortsEvidenceDetector {
    fun hasEvidence(packageName: String?, viewIdResourceName: String?): Boolean =
        packageName == "com.google.android.youtube" &&
            viewIdResourceName == "com.google.android.youtube:id/reel_player_page_container"
}
