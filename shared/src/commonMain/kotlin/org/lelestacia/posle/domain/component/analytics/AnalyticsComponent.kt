package org.lelestacia.posle.domain.component.analytics

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.AnalyticsEvent
import org.lelestacia.posle.domain.state_event.AnalyticsState

/**
 * Analytics dashboard component exposing sales and stock analytics
 * for a user-selectable date range.
 */
interface AnalyticsComponent {

    /**
     * The current analytics state (sales KPIs, stock movers, selected range).
     */
    val state: StateFlow<AnalyticsState>

    /**
     * Processes interaction events from the analytics UI.
     *
     * @param event The event triggered by the user (e.g., range selection).
     */
    fun onEvent(event: AnalyticsEvent)
}
