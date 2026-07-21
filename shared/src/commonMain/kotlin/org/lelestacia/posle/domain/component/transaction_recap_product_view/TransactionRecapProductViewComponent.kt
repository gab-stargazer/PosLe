package org.lelestacia.posle.domain.component.transaction_recap_product_view

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.model.TransactionProduct

interface TransactionRecapProductViewComponent {
    val state: StateFlow<TransactionRecapProductViewState>
    fun onEvent(event: TransactionRecapProductViewEvent)
}

data class TransactionRecapProductViewState(
    val transactionProducts: List<TransactionProduct>
)

sealed interface TransactionRecapProductViewEvent {
    data object OnPop : TransactionRecapProductViewEvent
}