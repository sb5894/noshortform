package com.example.no_shortform

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/** Development-only observation of YouTube event metadata. */
class YouTubeEventObserverService : AccessibilityService() {
    private val blockingSettings by lazy { BlockingSettings(this) }
    private val blockingPolicy = ShortsBlockingPolicy()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!BuildConfig.DEBUG || event == null) return
        val packageName = event.packageName?.toString()
        if (packageName != YOUTUBE_PACKAGE) return

        // Read only the source ID; never traverse nodes or read text/content descriptions.
        val source = event.source
        val viewId = try {
            source?.viewIdResourceName
        } finally {
            // Node pooling was removed in API 33. Older devices still require recycling.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                @Suppress("DEPRECATION")
                source?.recycle()
            }
        }

        val configuration = blockingSettings.snapshot()
        val shortsEvidence = ShortsEvidenceDetector.hasEvidence(packageName, viewId, configuration.mode)
        Log.d(
            LOG_TAG,
            "packageName=$packageName, " +
                "eventType=${AccessibilityEvent.eventTypeToString(event.eventType)}, " +
                "className=${event.className}, viewIdResourceName=$viewId, " +
                "shortsEvidence=$shortsEvidence"
        )

        blockingPolicy.tryBlock(
            enabled = configuration.enabled,
            shortsEvidence = shortsEvidence,
            eventTime = event.eventTime,
            now = SystemClock.uptimeMillis(),
            requestBack = { performGlobalAction(GLOBAL_ACTION_BACK) }
        )
    }

    override fun onInterrupt() {
        // No feedback, pending actions, or retained event data to stop.
    }

    private companion object {
        const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        const val LOG_TAG = "YouTubeEventObserver"
    }
}
