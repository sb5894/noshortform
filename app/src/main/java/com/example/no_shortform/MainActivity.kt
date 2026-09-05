package com.example.no_shortform

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.ComponentName
import android.content.Context
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.no_shortform.ui.theme.NoshortformTheme

class MainActivity : ComponentActivity() {
    private val blockingSettings by lazy { BlockingSettings(this) }
    private var blockingEnabled by mutableStateOf(false)
    private var blockingMode by mutableStateOf(BlockingMode.ENTRY)
    private var accessibilityEnabled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoshortformTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ObserverScreen(
                        onOpenSettings = ::openAccessibilitySettings,
                        blockingEnabled = blockingEnabled,
                        blockingMode = blockingMode,
                        onModeChanged = { mode ->
                            if (BuildConfig.DEBUG) {
                                blockingSettings.mode = mode
                                blockingMode = mode
                            }
                        },
                        accessibilityEnabled = accessibilityEnabled,
                        onBlockingChanged = { enabled ->
                            if (BuildConfig.DEBUG) {
                                blockingSettings.enabled = enabled
                                blockingEnabled = enabled
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val configuration = blockingSettings.snapshot()
        blockingEnabled = configuration.enabled
        blockingMode = configuration.mode
        val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val component = ComponentName(this, YouTubeEventObserverService::class.java)
        accessibilityEnabled = manager.isEnabled && manager
            .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { info ->
                val service = info.resolveInfo.serviceInfo
                ComponentName(service.packageName, service.name) == component
            }
    }

    private fun openAccessibilitySettings() {
        try {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.accessibility_settings_unavailable, Toast.LENGTH_LONG).show()
        }
    }
}
