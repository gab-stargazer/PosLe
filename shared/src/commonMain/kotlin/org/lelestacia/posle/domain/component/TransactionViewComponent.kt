package org.lelestacia.posle.domain.component

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.TransactionViewEvent
import org.lelestacia.posle.domain.state_event.TransactionViewState

/**
 * Component interface for viewing the details of a specific transaction.
 *
 * This component provides functionality to:
 * - View transaction metadata (Customer, Date, Totals).
 * - Review individual items and products in the transaction.
 * - Perform actions such as recapping or printing.
 */
interface TransactionViewComponent {
    /**
     * The detailed state of the transaction being viewed.
     */
    val state: StateFlow<TransactionViewState>

    /**
     * Processes view-specific events.
     *
     * @param event Interaction event (e.g., Navigate back, Mark as recapped).
     */
    fun onEvent(event: TransactionViewEvent)
}

/**
 * Navigation commands for the Transaction View.
 */
sealed interface TransactionViewNavigation {
    /**
     * Navigate back to the previous screen.
     */
    data object OnPop : TransactionViewNavigation
}
