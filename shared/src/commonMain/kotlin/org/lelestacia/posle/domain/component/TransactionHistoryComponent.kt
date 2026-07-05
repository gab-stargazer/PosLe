package org.lelestacia.posle.domain.component

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.navigation.Config

interface TransactionHistoryComponent {
    val allHistory: Flow<PagingData<Transaction>>
    val unRecappedHistory: Flow<PagingData<Transaction>>
    val state: StateFlow<TransactionHistoryScreenState>
    fun onEvent(event: TransactionHistoryScreenEvent)
}

sealed interface TransactionHistoryScreenEvent {
    data class OnNavigate(val config: Config) : TransactionHistoryScreenEvent
    data class OnTabSelected(val selectedTab: Int) : TransactionHistoryScreenEvent
}

data class TransactionHistoryScreenState(
    val selectedTab: Int = 0,
    val todayTransactions: List<Transaction> = emptyList(),
    val settings: PosLeSettings = PosLeSettings()
)
