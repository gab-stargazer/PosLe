package org.lelestacia.posle.domain.component.transaction_add

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.navigation.Config

interface TransactionAddNavigation {
    fun onNavigateTo(config: Config, onComplete: () -> Unit = {})

    fun onNavigateToProductConfig(
        product: Product,
        onConfirmed: (TransactionItemState, List<Variant>) -> Unit
    )

    fun onNavigateToQRScanner(
        onResult: (String) -> Unit
    )
}

/**
 * Component interface for the Transaction Add screen.
 * Manages the cart, product selection, and transaction finalization.
 */
interface TransactionAddComponent {
    /**
     * Paginated flow of available product bundles.
     */
    val bundles: Flow<PagingData<Bundle>>

    /**
     * Paginated flow of available products.
     */
    val products: Flow<PagingData<Product>>

    /**
     * The current UI state, including the active cart, customer info, and total price.
     */
    val state: StateFlow<TransactionAddState>

    /**
     * Handles UI events such as adding items to cart, updating quantities, and checkout.
     */
    fun onEvent(event: TransactionAddEvent)
}
