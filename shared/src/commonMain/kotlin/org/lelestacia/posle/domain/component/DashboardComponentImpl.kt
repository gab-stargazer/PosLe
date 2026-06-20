package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lelestacia.posle.domain.component.DashboardNavigation.DrawerNav
import org.lelestacia.posle.domain.component.DashboardNavigation.Nav
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardComponentState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavConfig
import org.lelestacia.posle.util.SelectedTabIndex
import org.lelestacia.posle.util.coroutineScope

class DashboardComponentImpl(
    componentContext: ComponentContext,
    private val navChildren: Value<ChildStack<NavConfig, NavChild>>,
    val onNavigation: (DashboardNavigation) -> Unit,
) : ComponentContext by componentContext, DashboardComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    override val children: Value<ChildStack<NavConfig, NavChild>>
        get() = navChildren

    override val state: StateFlow<DashboardComponentState>
        field = MutableStateFlow(DashboardComponentState())

    override fun onEvent(event: DashboardComponentEvent) {
        scope.launch {
            when (event) {
                is DashboardComponentEvent.OnMenuNavigateTo -> {
                    state.update { currentState ->
                        currentState.copy(
                            selectedTab = SelectedTabIndex(event.index)
                        )
                    }

                    onNavigation(DrawerNav(event.destination, callbacks = event.callbacks))
                }

                is DashboardComponentEvent.OnNavigateTo -> onNavigation(Nav(event.config))
            }
        }
    }
}

interface DashboardComponent {
    val children: Value<ChildStack<NavConfig, NavChild>>
    val state: StateFlow<DashboardComponentState>
    fun onEvent(event: DashboardComponentEvent)
}

sealed interface DashboardNavigation {
    data class DrawerNav(val navConfig: NavConfig, val callbacks: () -> Unit) : DashboardNavigation
    data class Nav(val config: Config) : DashboardNavigation
}

