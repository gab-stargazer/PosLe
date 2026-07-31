package org.lelestacia.posle.domain.component.dashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardComponentState
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavConfig
import org.lelestacia.posle.util.SelectedTabIndex
import org.lelestacia.posle.util.coroutineScope

class DashboardComponentImpl(
    componentContext: ComponentContext,
    navChildren: Value<ChildStack<NavConfig, NavChild>>,
    private val settingManager: SettingManager,
    val onNavigation: (DashboardNavigation) -> Unit,
) : ComponentContext by componentContext, DashboardComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    override val children: Value<ChildStack<NavConfig, NavChild>> = navChildren

    override val state: StateFlow<DashboardComponentState>
        field = MutableStateFlow(DashboardComponentState())

    init {
        scope.launch {
            settingManager.getSettings().first().run {
                state.update {
                    it.copy(
                        settings = this
                    )
                }
            }
        }
    }

    override fun onEvent(event: DashboardComponentEvent) {
        scope.launch {
            when (event) {
                is DashboardComponentEvent.OnMenuNavigateTo -> {
                    state.update { currentState ->
                        currentState.copy(
                            selectedTab = SelectedTabIndex(event.index)
                        )
                    }

                    onNavigation(
                        DashboardNavigation.DrawerNav(
                            event.destination,
                            callbacks = event.callbacks
                        )
                    )
                }

                is DashboardComponentEvent.OnNavigateTo -> onNavigation(
                    DashboardNavigation.Nav(
                        event.config
                    )
                )

                is DashboardComponentEvent.OnToggleProductMenu -> {
                    state.update { currentState ->
                        currentState.copy(
                            isProductMenuExpanded = event.isExpanded
                        )
                    }
                }
            }
        }
    }
}

