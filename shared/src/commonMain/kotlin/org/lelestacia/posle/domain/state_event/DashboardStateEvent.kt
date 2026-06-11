package org.lelestacia.posle.domain.state_event

import org.lelestacia.posle.navigation.NavConfig
import org.lelestacia.posle.util.SelectedTabIndex

data class DashboardStateEvent(
    val selectedTab: SelectedTabIndex = SelectedTabIndex(0)
)

sealed interface DashboardComponentEvent {
    data class OnNavigateTo(val index: Int, val destination: NavConfig): DashboardComponentEvent
}