package org.lelestacia.posle.ui.screen.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.analytics.DateRange
import org.lelestacia.posle.domain.component.analytics.AnalyticsComponent
import org.lelestacia.posle.domain.state_event.AnalyticsEvent
import org.lelestacia.posle.domain.state_event.AnalyticsState
import org.lelestacia.posle.ui.screen.analytics.component.AnalyticsFastMovers
import org.lelestacia.posle.ui.screen.analytics.component.AnalyticsKpiCard
import org.lelestacia.posle.ui.screen.analytics.component.AnalyticsRangePicker
import org.lelestacia.posle.ui.screen.analytics.component.AnalyticsRangePreset
import org.lelestacia.posle.ui.screen.analytics.component.AnalyticsRevenueChart
import org.lelestacia.posle.ui.screen.analytics.component.AnalyticsSlowMovers
import org.lelestacia.posle.ui.screen.analytics.component.AnalyticsTopProducts
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_confirm
import posle.shared.generated.resources.label_items_sold
import posle.shared.generated.resources.label_sales_section
import posle.shared.generated.resources.label_stock_section
import posle.shared.generated.resources.label_stock_tracking_disabled
import posle.shared.generated.resources.label_total_profit
import posle.shared.generated.resources.label_total_revenue
import posle.shared.generated.resources.label_total_transactions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    component: AnalyticsComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsStateWithLifecycle()

    if (state.isDateRangePickerShown) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                dayInSelectionRangeContainerColor = BurgundyRed.copy(0.5F),
                selectedDayContainerColor = BurgundyRed
            ),
            onDismissRequest = {
                component.onEvent(AnalyticsEvent.OnDateRangePickerVisibilityChanged(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = state.dateRangePickerState.selectedStartDateMillis
                        val finish = state.dateRangePickerState.selectedEndDateMillis
                        if (start != null && finish != null) {
                            component.onEvent(
                                AnalyticsEvent.OnDateRangeChanged(
                                    DateRange(
                                        startDate = start,
                                        finishDate = finish + 1
                                    )
                                )
                            )
                        }
                        component.onEvent(AnalyticsEvent.OnDateRangePickerVisibilityChanged(false))
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.btn_confirm),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        ) {
            DateRangePicker(
                state = state.dateRangePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.salesResult.overview.totalTransactions == 0) {
                CircularProgressIndicator(
                    color = BurgundyRed,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                )
            } else {
                AnalyticsContent(
                    state = state,
                    onEvent = component::onEvent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AnalyticsContent(
    state: AnalyticsState,
    onEvent: (AnalyticsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        item {
            AnalyticsRangePicker(
                selectedPreset = state.selectedPreset(),
                onPresetSelected = { preset ->
                    onEvent(
                        AnalyticsEvent.OnDateRangeChanged(
                            when (preset) {
                                AnalyticsRangePreset.Today -> DateRange.today()
                                AnalyticsRangePreset.Last7Days -> DateRange.last7Days()
                                AnalyticsRangePreset.Last30Days -> DateRange.last30Days()
                                else -> state.selectedRange
                            }
                        )
                    )
                },
                onCustomSelected = {
                    onEvent(AnalyticsEvent.OnDateRangePickerVisibilityChanged(true))
                }
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(title = stringResource(Res.string.label_sales_section))
                AnalyticsKpiRow(state = state)
                // A single-day range collapses to a one-point series, so the trend
                // chart adds no information — hide it for the "Hari Ini" preset.
                if (state.selectedPreset() != AnalyticsRangePreset.Today) {
                    AnalyticsRevenueChart(timeSeries = state.salesResult.timeSeries)
                }
                AnalyticsTopProducts(topProducts = state.salesResult.topProducts)
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(title = stringResource(Res.string.label_stock_section))
                if (state.isStockTrackingEnabled) {
                    AnalyticsFastMovers(movers = state.stockResult.fastMovers)
                    AnalyticsSlowMovers(movers = state.stockResult.slowMovers)
                } else {
                    Text(
                        text = stringResource(Res.string.label_stock_tracking_disabled),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun AnalyticsKpiRow(state: AnalyticsState) {
    val overview = state.salesResult.overview
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        AnalyticsKpiCard(
            label = stringResource(Res.string.label_total_revenue),
            value = overview.totalRevenue.toRupiah(),
            modifier = Modifier.size(width = 150.dp, height = 100.dp)
        )
        AnalyticsKpiCard(
            label = stringResource(Res.string.label_total_profit),
            value = overview.totalProfit.toRupiah(),
            modifier = Modifier.size(width = 150.dp, height = 100.dp)
        )
        AnalyticsKpiCard(
            label = stringResource(Res.string.label_total_transactions),
            value = overview.totalTransactions.toString(),
            modifier = Modifier.size(width = 150.dp, height = 100.dp)
        )
        AnalyticsKpiCard(
            label = stringResource(Res.string.label_items_sold),
            value = overview.totalItemsSold.toDisplayText(),
            modifier = Modifier.size(width = 150.dp, height = 100.dp)
        )
    }
}

/**
 * Maps the current [AnalyticsState.selectedRange] back to a preset for the chip row.
 */
private fun AnalyticsState.selectedPreset(): AnalyticsRangePreset {
    val today = DateRange.today()
    return when (selectedRange) {
        DateRange.today() -> AnalyticsRangePreset.Today
        DateRange.last7Days() -> AnalyticsRangePreset.Last7Days
        DateRange.last30Days() -> AnalyticsRangePreset.Last30Days
        else -> {
            val isCustom = today.startDate != selectedRange.startDate
            if (isCustom) AnalyticsRangePreset.Custom else AnalyticsRangePreset.Today
        }
    }
}

@Preview
@Composable
private fun PreviewAnalyticsScreen() {
    AppTheme {
        AnalyticsScreen(
            component = object : AnalyticsComponent {
                override val state: MutableStateFlow<AnalyticsState> = MutableStateFlow(
                    AnalyticsState(
                        isStockTrackingEnabled = true,
                        isLoading = false,
                    )
                )

                override fun onEvent(event: AnalyticsEvent) {}
            }
        )
    }
}
