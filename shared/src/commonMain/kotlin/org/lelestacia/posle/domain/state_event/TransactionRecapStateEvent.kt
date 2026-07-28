package org.lelestacia.posle.domain.state_event

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DateRangePickerState
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Transaction

data class TransactionRecapState(
    val transactionHistory: List<Transaction> = listOf(),
    val listOfProducts: List<List<org.lelestacia.posle.navigation.Config.TransactionRecapProductItem>> = listOf(),
    val totalProfit: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    val startDate: Long = 0,
    val finishDate: Long = 0,
    val isSameDay: Boolean = false,
    val selectedPrimaryTab: Int = 0,
    val searchQuery: String = "",
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
    data class OnNavigateToRecapProductView(val products: List<org.lelestacia.posle.navigation.Config.TransactionRecapProductItem>) :
        TransactionRecapEvent

    data class OnDateRangePickerVisibilityChanged(val isShown: Boolean) : TransactionRecapEvent
    data class OnDateRangeChanged(val dateRange: Pair<Long, Long>) : TransactionRecapEvent
    data class OnSearchQueryChanged(val query: String) : TransactionRecapEvent
    data object OnPrintRecap : TransactionRecapEvent
}
