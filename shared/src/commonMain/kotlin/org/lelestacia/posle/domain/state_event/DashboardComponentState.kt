package org.lelestacia.posle.domain.state_event

import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.NavConfig
import org.lelestacia.posle.util.SelectedTabIndex

data class DashboardComponentState(
    val selectedTab: SelectedTabIndex = SelectedTabIndex(0)
)

sealed interface DashboardComponentEvent {
    data class OnMenuNavigateTo(val index: Int, val destination: NavConfig): DashboardComponentEvent
    data class OnNavigateTo(val config: Config): DashboardComponentEvent
}