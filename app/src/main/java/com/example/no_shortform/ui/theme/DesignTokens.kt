package com.example.no_shortform.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shared visual values. Components consume roles instead of literal colors and dimensions. */
internal object DesignTokens {
    val lightColors = lightColorScheme(
        primary = Color(0xFF3F6444), onPrimary = Color.White,
        primaryContainer = Color(0xFFDDEDCB), onPrimaryContainer = Color(0xFF233923),
        secondary = Color(0xFF526050), onSecondary = Color.White,
        secondaryContainer = Color(0xFFE5EFCB), onSecondaryContainer = Color(0xFF233923),
        tertiary = Color(0xFF65512B), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFF2EAD4), onTertiaryContainer = Color(0xFF65512B),
        background = Color(0xFFF5F8EF), onBackground = Color(0xFF233127),
        surface = Color(0xFFFAFCF6), onSurface = Color(0xFF233127),
        surfaceVariant = Color(0xFFE4EBDD), onSurfaceVariant = Color(0xFF526050),
        outline = Color(0xFF74816E), outlineVariant = Color(0xFFC7D2BF),
        inverseSurface = Color(0xFF29352C), inverseOnSurface = Color(0xFFE5ECE1),
        inversePrimary = Color(0xFFB8D9A8), surfaceTint = Color(0xFF3F6444),
        surfaceDim = Color(0xFFD9E1D3), surfaceBright = Color(0xFFFAFCF6),
        surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFF0F5E9),
        surfaceContainer = Color(0xFFEBF1E3), surfaceContainerHigh = Color(0xFFE4EBDD),
        surfaceContainerHighest = Color(0xFFDDE6D5)
    )
    val darkColors = darkColorScheme(
        primary = Color(0xFFB8D9A8), onPrimary = Color(0xFF18351F),
        primaryContainer = Color(0xFF30452F), onPrimaryContainer = Color(0xFFDFEED6),
        secondary = Color(0xFFB5C1B0), onSecondary = Color(0xFF263424),
        secondaryContainer = Color(0xFF293E30), onSecondaryContainer = Color(0xFFDFEED6),
        tertiary = Color(0xFFE6D5A6), onTertiary = Color(0xFF382D16),
        tertiaryContainer = Color(0xFF3D3523), onTertiaryContainer = Color(0xFFE6D5A6),
        background = Color(0xFF141B16), onBackground = Color(0xFFE5ECE1),
        surface = Color(0xFF202A22), onSurface = Color(0xFFE5ECE1),
        surfaceVariant = Color(0xFF344232), onSurfaceVariant = Color(0xFFB5C1B0),
        outline = Color(0xFF899A83), outlineVariant = Color(0xFF43523F),
        inverseSurface = Color(0xFFE5ECE1), inverseOnSurface = Color(0xFF233127),
        inversePrimary = Color(0xFF3F6444), surfaceTint = Color(0xFFB8D9A8),
        surfaceDim = Color(0xFF141B16), surfaceBright = Color(0xFF354137),
        surfaceContainerLowest = Color(0xFF101611), surfaceContainerLow = Color(0xFF1A231C),
        surfaceContainer = Color(0xFF202A22), surfaceContainerHigh = Color(0xFF29352B),
        surfaceContainerHighest = Color(0xFF344034)
    )
    val lightAmbient = Color(0xFFD5E8B7)
    val darkAmbient = Color(0xFF344C31)
    const val lightGlassAlpha = 0.54f
    const val darkGlassAlpha = 0.64f
    const val glassHighlightAlpha = 0.22f
    const val glassEdgeAlpha = 0.74f
    const val glassEdgeFadeAlpha = 0.12f
    const val glassShadowAlpha = 0.08f
    const val selectedEdgeAlpha = 0.70f
    const val statusTextWeight = 0.44f
    const val statusButtonWeight = 0.56f
    val glassHighlight = Color.White
    val glassShadow = Color(0xFF233923)
    val glassElevation = 4.dp
    const val ambientCenterX = 0.12f
    const val ambientCenterY = 0.12f
    const val ambientSecondX = 0.94f
    const val ambientSecondY = 0.66f
    const val ambientRadiusFraction = 0.90f
    const val ambientSecondRadiusFraction = 0.72f
    val smallGap = 8.dp
    val itemGap = 12.dp
    val compactPadding = 16.dp
    val surfacePadding = 20.dp
    val screenPadding = 24.dp
    val sectionGap = 28.dp
    val touchTarget = 48.dp
    val maxContentWidth = 560.dp
    val narrowWidth = 360.dp
    val borderWidth = 1.dp
    val glassShape = RoundedCornerShape(24.dp)
    val modeShape = RoundedCornerShape(20.dp)

    private fun text(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = weight,
        fontSize = size.sp, lineHeight = height.sp, letterSpacing = 0.sp
    )
    val typography = Typography(
        headlineSmall = text(26, 34, FontWeight.SemiBold),
        titleLarge = text(20, 28, FontWeight.Medium),
        titleMedium = text(16, 24, FontWeight.SemiBold),
        titleSmall = text(16, 24, FontWeight.SemiBold),
        bodyLarge = text(16, 24), bodyMedium = text(14, 22),
        labelLarge = text(14, 20, FontWeight.Medium)
    )
}
