package org.lelestacia.posle.domain.component

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.state_event.TransactionSearchEvent
import org.lelestacia.posle.domain.state_event.TransactionSearchState

interface TransactionSearchComponent {
    val state: StateFlow<TransactionSearchState>
    val searchResults: Flow<PagingData<Transaction>>
    fun onEvent(event: TransactionSearchEvent)
}
