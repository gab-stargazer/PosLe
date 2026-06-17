package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardComponentState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavConfig
import org.lelestacia.posle.util.SelectedTabIndex
import org.lelestacia.posle.util.coroutineScope

class DashboardComponent(
    componentContext: ComponentContext,
    val children: Value<ChildStack<NavConfig, NavChild>>,
    val onNavigation: (DashboardNavigation) -> Unit,
) : ComponentContext by componentContext{

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    val state: Value<DashboardComponentState>
        field = MutableValue(DashboardComponentState())

    fun onEvent(event: DashboardComponentEvent) = scope.launch {
        when (event) {
            is DashboardComponentEvent.OnNavigateTo -> {
                state.update { currentState ->
                    currentState.copy(
                        selectedTab = SelectedTabIndex(event.index)
                    )
                }

                onNavigation(DashboardNavigation.BottomNav(event.destination))
            }
        }
    }
}

sealed interface DashboardNavigation {
    data class BottomNav(val navConfig: NavConfig) : DashboardNavigation
    data class Nav(val config: Config) : DashboardNavigation
}

