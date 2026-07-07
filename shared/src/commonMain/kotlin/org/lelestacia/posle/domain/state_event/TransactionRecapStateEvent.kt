package org.lelestacia.posle.domain.state_event

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DateRangePickerState
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Transaction

data class TransactionRecapState(
    val transactionHistory: List<Transaction> = listOf(),
    val startDate: Long = 0,
    val finishDate: Long = 0,
    val isSameDay: Boolean = false,
    val selectedPrimaryTab: Int = 0,
    val settings: PosLeSettings = PosLeSettings(),

    //  Date Range Picker
    val isDateRangePickerShown: Boolean = false,
    val dateRangePickerState: DateRangePickerState = DateRangePickerState(
        locale = CalendarLocale.forLanguageTag("id-ID")
    )
)

sealed interface TransactionRecapEvent {
    data class OnPrimaryTabChanged(val newIndex: Int) : TransactionRecapEvent
    data class OnNavigateToTransactionView(val transaction: Transaction) : TransactionRecapEvent
    data class OnDateRangePickerVisibilityChanged(val isShown: Boolean) : TransactionRecapEvent
    data class OnDateRangeChanged(val dateRange: Pair<Long, Long>) : TransactionRecapEvent
}