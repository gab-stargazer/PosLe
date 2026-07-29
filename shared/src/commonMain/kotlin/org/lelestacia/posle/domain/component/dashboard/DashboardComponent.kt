package org.lelestacia.posle.domain.component.dashboard

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardComponentState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavConfig

/**
 * Main application component that manages high-level navigation and state.
 *
 * It serves as the root for the dashboard UI, hosting multiple tabs and handling
 * both drawer-based and stack-based navigation.
 */
interface DashboardComponent {
    /**
     * The stack of child components (tabs) managed by this dashboard.
     */
    val children: Value<ChildStack<NavConfig, NavChild>>

    /**
     * The high-level state of the dashboard, including the currently selected tab.
     */
    val state: StateFlow<DashboardComponentState>

    /**
     * Processes interaction events from the dashboard UI.
     *
     * @param event The event triggered by the user (e.g., menu navigation).
     */
    fun onEvent(event: DashboardComponentEvent)
}

/**
 * Navigation instructions emitted by the Dashboard component to its parent.
 */
sealed interface DashboardNavigation {
    /**
     * Navigation triggered from the side drawer (usually tab switches).
     */
    data class DrawerNav(val navConfig: NavConfig, val callbacks: () -> Unit) : DashboardNavigation

    /**
     * Deep navigation to a specific sub-screen outside the tab flow.
     */
    data class Nav(val config: Config) : DashboardNavigation
}
