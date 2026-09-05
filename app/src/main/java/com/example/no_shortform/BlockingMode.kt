package com.example.no_shortform

internal enum class BlockingMode {
    LEGACY,
    ENTRY;

    companion object {
        fun fromStoredValue(value: String?): BlockingMode =
            entries.firstOrNull { it.name == value } ?: ENTRY
    }
}

internal data class BlockingConfiguration(val enabled: Boolean, val mode: BlockingMode)
