package org.lelestacia.posle.ui.screen.analytics.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.ui.theme.BurgundyRed
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_analytics_range_custom
import posle.shared.generated.resources.label_analytics_range_last_30_days
import posle.shared.generated.resources.label_analytics_range_last_7_days
import posle.shared.generated.resources.label_analytics_range_today

/**
 * Preset analytics date ranges, mirroring the filter chips in the UI.
 */
enum class AnalyticsRangePreset(val title: StringResource) {
    Today(Res.string.label_analytics_range_today),
    Last7Days(Res.string.label_analytics_range_last_7_days),
    Last30Days(Res.string.label_analytics_range_last_30_days),
    Custom(Res.string.label_analytics_range_custom),
}

/**
 * A horizontal row of filter chips for selecting the analytics date range.
 *
 * @param selectedPreset The currently selected preset.
 * @param onPresetSelected Callback when a preset chip is clicked.
 * @param onCustomSelected Callback when the "Kustom" chip is clicked (opens the picker).
 */
@Composable
fun AnalyticsRangePicker(
    selectedPreset: AnalyticsRangePreset,
    onPresetSelected: (AnalyticsRangePreset) -> Unit,
    onCustomSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        AnalyticsRangePreset.entries.forEach { preset ->
            val selected = selectedPreset == preset
            FilterChip(
                selected = selected,
                onClick = {
                    if (preset == AnalyticsRangePreset.Custom) {
                        onCustomSelected()
                    } else {
                        onPresetSelected(preset)
                    }
                },
                label = {
                    Text(
                        text = stringResource(preset.title),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BurgundyRed,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    }
}
