package com.example.no_shortform.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal data class CalmSurfaces(val ambient: Color, val glassAlpha: Float)

internal val LocalCalmSurfaces = staticCompositionLocalOf {
    CalmSurfaces(DesignTokens.lightAmbient, DesignTokens.lightGlassAlpha)
}

@Composable
fun NoshortformTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val surfaces = if (darkTheme) {
        CalmSurfaces(DesignTokens.darkAmbient, DesignTokens.darkGlassAlpha)
    } else {
        CalmSurfaces(DesignTokens.lightAmbient, DesignTokens.lightGlassAlpha)
    }
    CompositionLocalProvider(LocalCalmSurfaces provides surfaces) {
        MaterialTheme(
            colorScheme = if (darkTheme) DesignTokens.darkColors else DesignTokens.lightColors,
            typography = DesignTokens.typography,
            content = content
        )
    }
}
