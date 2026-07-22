package org.lelestacia.posle.domain.component.transaction_recap_product_view

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.navigation.Config.TransactionRecapProductItem

interface TransactionRecapProductViewComponent {
    val state: StateFlow<TransactionRecapProductViewState>
    fun onEvent(event: TransactionRecapProductViewEvent)
}

data class TransactionRecapProductViewState(
    val transactionProducts: List<TransactionRecapProductItem>
)

sealed interface TransactionRecapProductViewEvent {
    data object OnPop : TransactionRecapProductViewEvent
}