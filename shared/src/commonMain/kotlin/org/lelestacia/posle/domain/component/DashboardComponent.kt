package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardComponentState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavConfig

interface DashboardComponent {
    val children: Value<ChildStack<NavConfig, NavChild>>
    val state: StateFlow<DashboardComponentState>
    fun onEvent(event: DashboardComponentEvent)
}

sealed interface DashboardNavigation {
    data class DrawerNav(val navConfig: NavConfig, val callbacks: () -> Unit) : DashboardNavigation
    data class Nav(val config: Config) : DashboardNavigation
}
