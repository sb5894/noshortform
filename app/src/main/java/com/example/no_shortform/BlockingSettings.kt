package com.example.no_shortform

import android.content.Context
import androidx.core.content.edit

internal class BlockingSettings(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences("blocking_settings", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = preferences.getBoolean("enabled", false)
        set(value) { preferences.edit { putBoolean("enabled", value) } }

    var mode: BlockingMode
        get() = BlockingMode.fromStoredValue(preferences.all["mode"] as? String)
        set(value) { preferences.edit { putString("mode", value.name) } }

    fun snapshot(): BlockingConfiguration {
        val values = preferences.all
        return BlockingConfiguration(
            enabled = values["enabled"] as? Boolean ?: false,
            mode = BlockingMode.fromStoredValue(values["mode"] as? String)
        )
    }
}
