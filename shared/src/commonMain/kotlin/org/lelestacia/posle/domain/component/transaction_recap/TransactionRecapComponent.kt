package org.lelestacia.posle.domain.component.transaction_recap

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent
import org.lelestacia.posle.domain.state_event.TransactionRecapState

/**
 * Component interface for the Sales Recap screen.
 *
 * Calculates and displays sales performance over a specific period, including:
 * - Total revenue and profit.
 * - Product-wise sales breakdown.
 * - Individual transaction logs for the period.
 */
interface TransactionRecapComponent {
    /**
     * Observable state containing calculations, transaction lists, and period filters.
     */
    val state: StateFlow<TransactionRecapState>

    /**
     * Processes recap events (e.g., Change date range, Print summary).
     */
    fun onEvent(event: TransactionRecapEvent)
}