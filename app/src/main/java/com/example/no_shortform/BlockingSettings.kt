package com.example.no_shortform

import android.content.Context
import androidx.core.content.edit

internal class BlockingSettings(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences("blocking_settings", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = preferences.getBoolean("enabled", false)
        set(value) { preferences.edit { putBoolean("enabled", value) } }
}
