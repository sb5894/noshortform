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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.no_shortform.ui.theme.NoshortformTheme

class MainActivity : ComponentActivity() {
    private val blockingSettings by lazy { BlockingSettings(this) }
    private var blockingEnabled by mutableStateOf(false)
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
        blockingEnabled = blockingSettings.enabled
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

@Composable
fun ObserverScreen(
    onOpenSettings: () -> Unit,
    blockingEnabled: Boolean,
    accessibilityEnabled: Boolean,
    onBlockingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val blockingLabel = stringResource(R.string.blocking_label)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(stringResource(R.string.observer_title), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.observer_description))
        Text(stringResource(
            if (accessibilityEnabled) R.string.accessibility_enabled
            else R.string.accessibility_disabled
        ))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(blockingLabel, modifier = Modifier.weight(1f))
            Switch(
                modifier = Modifier.semantics { contentDescription = blockingLabel },
                checked = blockingEnabled,
                onCheckedChange = onBlockingChanged,
                enabled = BuildConfig.DEBUG
            )
        }
        Text(stringResource(
            if (!BuildConfig.DEBUG) R.string.blocking_debug_only
            else if (blockingEnabled) R.string.blocking_enabled_notice
            else R.string.blocking_disabled_notice
        ))
        Text(stringResource(R.string.observer_data_notice))
        Text(stringResource(R.string.observer_scope_notice))
        Text(stringResource(R.string.observer_instructions))
        Button(onClick = onOpenSettings) {
            Text(stringResource(R.string.open_accessibility_settings))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ObserverScreenPreview() {
    NoshortformTheme {
        ObserverScreen(
            onOpenSettings = {},
            blockingEnabled = false,
            accessibilityEnabled = false,
            onBlockingChanged = {}
        )
    }
}
