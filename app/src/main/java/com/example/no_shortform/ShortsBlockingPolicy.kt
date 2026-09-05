package com.example.no_shortform

/** All timestamps use uptimeMillis, matching AccessibilityEvent.eventTime. */
internal class ShortsBlockingPolicy {
    private var lastAttemptTime: Long? = null

    fun tryBlock(
        enabled: Boolean,
        shortsEvidence: Boolean,
        eventTime: Long,
        now: Long,
        requestBack: () -> Boolean
    ) {
        if (!enabled || !shortsEvidence) return
        if (eventTime < 0 || now < eventTime || now - eventTime > 1_000L) return
        val lastAttempt = lastAttemptTime
        if (lastAttempt != null && now - lastAttempt < 2_000L) return

        // Reserve the interval before calling Android, including failed requests.
        lastAttemptTime = now
        requestBack()
        // A successful request does not prove that Shorts has been exited.
    }
}
