package org.lelestacia.posle.domain.component

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.TransactionViewEvent
import org.lelestacia.posle.domain.state_event.TransactionViewState

interface TransactionViewComponent {
    val state: StateFlow<TransactionViewState>
    fun onEvent(event: TransactionViewEvent)
    fun onAction(navigation: TransactionViewNavigation)
}

sealed interface TransactionViewNavigation {
    data object OnPop : TransactionViewNavigation
}
