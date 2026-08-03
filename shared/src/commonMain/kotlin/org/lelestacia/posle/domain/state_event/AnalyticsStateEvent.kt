package org.lelestacia.posle.domain.state_event

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DateRangePickerState
import org.lelestacia.posle.domain.analytics.AnalyticsResult
import org.lelestacia.posle.domain.analytics.DateRange
import org.lelestacia.posle.domain.analytics.StockAnalyticsResult

data class AnalyticsState(
    val salesResult: AnalyticsResult = AnalyticsResult(),
    val stockResult: StockAnalyticsResult = StockAnalyticsResult(),
    val isStockTrackingEnabled: Boolean = false,
    val selectedRange: DateRange = DateRange.last7Days(),
    val isDateRangePickerShown: Boolean = false,
    val dateRangePickerState: DateRangePickerState = DateRangePickerState(
        locale = CalendarLocale.forLanguageTag("id-ID")
    ),
    val isLoading: Boolean = true,
)

sealed interface AnalyticsEvent {
    data class OnDateRangePickerVisibilityChanged(val isShown: Boolean) : AnalyticsEvent
    data class OnDateRangeChanged(val range: DateRange) : AnalyticsEvent
}
