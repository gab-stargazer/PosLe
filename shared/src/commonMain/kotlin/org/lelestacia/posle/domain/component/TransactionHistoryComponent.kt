package org.lelestacia.posle.domain.component

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.navigation.Config

/**
 * Component interface for the Transaction History screen.
 *
 * Manages multiple views of transaction data:
 * - All historical records.
 * - Transactions pending recap.
 * - Today's specific transactions.
 */
interface TransactionHistoryComponent {
    /**
     * Paginated flow of all transactions.
     */
    val allHistory: Flow<PagingData<Transaction>>

    /**
     * Paginated flow of transactions that haven't been recapped.
     */
    val unRecappedHistory: Flow<PagingData<Transaction>>

    /**
     * Current screen state including tab selection and settings.
     */
    val state: StateFlow<TransactionHistoryScreenState>

    /**
     * Handles navigation and tab switching.
     */
    fun onEvent(event: TransactionHistoryScreenEvent)
}

/**
 * Events for the Transaction History screen.
 */
sealed interface TransactionHistoryScreenEvent {
    /**
     * Navigate to a detailed view of a transaction.
     */
    data class OnNavigate(val config: Config) : TransactionHistoryScreenEvent

    /**
     * Switch between history tabs (All, Pending, Today).
     */
    data class OnTabSelected(val selectedTab: Int) : TransactionHistoryScreenEvent
}

/**
 * UI State for the Transaction History screen.
 */
data class TransactionHistoryScreenState(
    val selectedTab: Int = 0,
    val todayTransactions: List<Transaction> = emptyList(),
    val settings: PosLeSettings = PosLeSettings()
)
