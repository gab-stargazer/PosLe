package org.lelestacia.posle.domain.component

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.state_event.TransactionSearchEvent
import org.lelestacia.posle.domain.state_event.TransactionSearchState

/**
 * Component interface for the Transaction Search screen.
 * Orchestrates the search logic and provides state to the UI.
 */
interface TransactionSearchComponent {
    /**
     * The current UI state, including search query and app settings.
     */
    val state: StateFlow<TransactionSearchState>

    /**
     * A flow of paginated transaction results matching the current search query.
     */
    val searchResults: Flow<PagingData<Transaction>>

    /**
     * Handles UI events such as query changes and navigation.
     */
    fun onEvent(event: TransactionSearchEvent)
}
