package org.lelestacia.posle.domain.component.transaction_recap

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent
import org.lelestacia.posle.domain.state_event.TransactionRecapState

interface TransactionRecapComponent {
    val state: StateFlow<TransactionRecapState>
    fun onEvent(event: TransactionRecapEvent)
}