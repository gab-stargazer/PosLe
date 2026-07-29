package org.lelestacia.posle.domain.component.transaction_recap_product_view

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.navigation.Config.TransactionRecapProductItem

/**
 * Component interface for viewing specific products within a recap period.
 */
interface TransactionRecapProductViewComponent {
    /**
     * Current view state containing the list of products.
     */
    val state: StateFlow<TransactionRecapProductViewState>

    /**
     * Processes view events.
     */
    fun onEvent(event: TransactionRecapProductViewEvent)
}

/**
 * UI State for the Recap Product View.
 *
 * @property transactionProducts List of products and their sold quantities in the period.
 */
data class TransactionRecapProductViewState(
    val transactionProducts: List<TransactionRecapProductItem>
)

/**
 * Events for the Recap Product View.
 */
sealed interface TransactionRecapProductViewEvent {
    /**
     * Close the view and return to the main recap screen.
     */
    data object OnPop : TransactionRecapProductViewEvent
}