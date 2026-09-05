package com.example.no_shortform

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.example.no_shortform.ui.theme.DesignTokens
import com.example.no_shortform.ui.theme.LocalCalmSurfaces
import com.example.no_shortform.ui.theme.NoshortformTheme

@Composable
internal fun ObserverScreen(
    onOpenSettings: () -> Unit,
    blockingEnabled: Boolean,
    blockingMode: BlockingMode,
    onModeChanged: (BlockingMode) -> Unit,
    accessibilityEnabled: Boolean,
    onBlockingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val surfaces = LocalCalmSurfaces.current
    BoxWithConstraints(
        modifier = modifier.fillMaxSize().background(colors.background).drawWithCache {
            val first = Brush.radialGradient(
                listOf(surfaces.ambient, surfaces.ambient.copy(alpha = 0f)),
                center = Offset(size.width * DesignTokens.ambientCenterX, size.height * DesignTokens.ambientCenterY),
                radius = (size.width * DesignTokens.ambientRadiusFraction).coerceAtLeast(1f)
            )
            val second = Brush.radialGradient(
                listOf(colors.secondaryContainer, colors.secondaryContainer.copy(alpha = 0f)),
                center = Offset(size.width * DesignTokens.ambientSecondX, size.height * DesignTokens.ambientSecondY),
                radius = (size.width * DesignTokens.ambientSecondRadiusFraction).coerceAtLeast(1f)
            )
            onDrawBehind {
                drawRect(first)
                drawRect(second)
            }
        },
        contentAlignment = Alignment.TopCenter
    ) {
        val horizontalPadding = if (maxWidth < DesignTokens.narrowWidth) {
            DesignTokens.compactPadding
        } else DesignTokens.screenPadding
        Column(
            modifier = Modifier.widthIn(max = DesignTokens.maxContentWidth).fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = DesignTokens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.sectionGap)
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge,
                color = colors.onBackground, modifier = Modifier.semantics { heading() })
            AccessibilityStatus(accessibilityEnabled, onOpenSettings)
            BlockingControl(blockingEnabled, accessibilityEnabled, onBlockingChanged)
            Column(verticalArrangement = Arrangement.spacedBy(DesignTokens.itemGap)) {
                Text(stringResource(R.string.blocking_mode_title), style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() })
                Column(
                    modifier = Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(DesignTokens.itemGap)
                ) {
                    BlockingMode.entries.forEach { mode ->
                        ModeOption(mode, blockingMode == mode) { onModeChanged(mode) }
                    }
                }
            }
            InformationSection()
        }
    }
}

@Composable
private fun AccessibilityStatus(enabled: Boolean, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignTokens.itemGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(if (enabled) R.string.service_status_on else R.string.service_status_off),
            modifier = Modifier.weight(DesignTokens.statusTextWeight),
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        val buttonModifier = Modifier.weight(DesignTokens.statusButtonWeight)
            .heightIn(min = DesignTokens.touchTarget)
        val buttonPadding = PaddingValues(horizontal = DesignTokens.itemGap, vertical = DesignTokens.smallGap)
        if (enabled) {
            OutlinedButton(onClick = onOpenSettings, modifier = buttonModifier, contentPadding = buttonPadding) {
                Text(stringResource(R.string.open_accessibility_settings))
            }
        } else {
            Button(onClick = onOpenSettings, modifier = buttonModifier, contentPadding = buttonPadding) {
                Text(stringResource(R.string.open_accessibility_settings))
            }
        }
    }
}

@Composable
private fun BlockingControl(enabled: Boolean, accessibilityEnabled: Boolean, onChanged: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val blockingLabel = stringResource(R.string.blocking_label)
    val notice = when {
        !BuildConfig.DEBUG -> R.string.blocking_debug_only
        !accessibilityEnabled && enabled -> R.string.status_waiting_enabled
        !accessibilityEnabled -> R.string.status_waiting_disabled
        enabled -> R.string.status_blocking_enabled
        else -> R.string.status_blocking_disabled
    }
    Surface(
        modifier = Modifier.fillMaxWidth().glassBlock(), shape = DesignTokens.glassShape,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.surfacePadding),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.itemGap),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(blockingLabel, style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() })
            Switch(
                checked = enabled, onCheckedChange = onChanged, enabled = BuildConfig.DEBUG,
                modifier = Modifier.heightIn(min = DesignTokens.touchTarget)
                    .semantics { contentDescription = blockingLabel }
            )
            Text(stringResource(notice), style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ModeOption(mode: BlockingMode, selected: Boolean, onSelected: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().glassBlock(DesignTokens.modeShape, selected)
            .selectable(selected = selected, enabled = BuildConfig.DEBUG, role = Role.RadioButton, onClick = onSelected)
            .padding(DesignTokens.compactPadding),
        horizontalArrangement = Arrangement.spacedBy(DesignTokens.itemGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(DesignTokens.smallGap)) {
            Text(
                stringResource(if (mode == BlockingMode.LEGACY) R.string.mode_legacy_title else R.string.mode_entry_title),
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) colors.onPrimaryContainer else colors.onSurface
            )
            Text(
                stringResource(if (mode == BlockingMode.LEGACY) R.string.mode_legacy_description else R.string.mode_entry_description),
                style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant
            )
        }
        RadioButton(selected = selected, onClick = null, enabled = BuildConfig.DEBUG)
    }
}

@Composable
private fun InformationSection() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val expansionState = stringResource(if (expanded) R.string.info_expanded else R.string.info_collapsed)
    Column(verticalArrangement = Arrangement.spacedBy(DesignTokens.itemGap)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        TextButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.align(Alignment.CenterHorizontally).heightIn(min = DesignTokens.touchTarget)
                .semantics { stateDescription = expansionState }
        ) {
            Text(stringResource(if (expanded) R.string.info_hide else R.string.info_show))
        }
        if (expanded) {
            InformationGroup(R.string.info_usage_title, listOf(R.string.info_usage))
            InformationGroup(R.string.info_data_title, listOf(R.string.observer_data_notice))
            InformationGroup(R.string.info_scope_title, listOf(
                R.string.info_build_scope, R.string.blocking_enabled_notice,
                R.string.blocking_disabled_notice, R.string.observer_scope_notice
            ))
        }
    }
}

@Composable
private fun InformationGroup(title: Int, paragraphs: List<Int>) {
    Column(
        modifier = Modifier.fillMaxWidth().glassBlock().padding(DesignTokens.compactPadding),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.smallGap)
    ) {
        Text(stringResource(title), style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() })
        paragraphs.forEach { paragraph ->
            Text(stringResource(paragraph), style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** A translucent tint and directional reflection, with no live blur or extra rendering loop. */
@Composable
private fun Modifier.glassBlock(shape: Shape = DesignTokens.glassShape, selected: Boolean = false): Modifier {
    val colors = MaterialTheme.colorScheme
    val alpha = LocalCalmSurfaces.current.glassAlpha
    val tint = if (selected) colors.primaryContainer else colors.surface
    val reflection = Brush.linearGradient(listOf(
        DesignTokens.glassHighlight.copy(alpha = DesignTokens.glassHighlightAlpha),
        DesignTokens.glassHighlight.copy(alpha = 0f),
        DesignTokens.glassHighlight.copy(alpha = DesignTokens.glassEdgeFadeAlpha)
    ))
    val edge = Brush.linearGradient(listOf(
        DesignTokens.glassHighlight.copy(alpha = DesignTokens.glassEdgeAlpha),
        if (selected) colors.primary.copy(alpha = DesignTokens.selectedEdgeAlpha)
        else colors.outlineVariant.copy(alpha = DesignTokens.glassEdgeFadeAlpha),
        DesignTokens.glassHighlight.copy(alpha = DesignTokens.glassHighlightAlpha)
    ))
    return shadow(
        DesignTokens.glassElevation, shape,
        ambientColor = DesignTokens.glassShadow.copy(alpha = DesignTokens.glassShadowAlpha),
        spotColor = DesignTokens.glassShadow.copy(alpha = DesignTokens.glassShadowAlpha)
    ).clip(shape).background(tint.copy(alpha = alpha)).background(reflection)
        .border(DesignTokens.borderWidth, edge, shape)
}

@Preview(name = "Light - setup", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(name = "Large text", showBackground = true, widthDp = 320, heightDp = 640, fontScale = 2f)
@Composable
fun ObserverScreenPreview() {
    NoshortformTheme(darkTheme = false) {
        ObserverScreen({}, false, BlockingMode.ENTRY, {}, false, {})
    }
}

@Preview(name = "Dark - enabled", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ObserverScreenDarkPreview() {
    NoshortformTheme(darkTheme = true) {
        ObserverScreen({}, true, BlockingMode.LEGACY, {}, true, {})
    }
}
