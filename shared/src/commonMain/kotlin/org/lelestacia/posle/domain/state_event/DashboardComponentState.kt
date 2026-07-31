package org.lelestacia.posle.domain.state_event

import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.NavConfig
import org.lelestacia.posle.util.SelectedTabIndex

data class DashboardComponentState(
    val selectedTab: SelectedTabIndex = SelectedTabIndex(0),
    val settings: PosLeSettings = PosLeSettings(),
    val isProductMenuExpanded: Boolean = false
)

sealed interface DashboardComponentEvent {
    data class OnMenuNavigateTo(val index: Int, val destination: NavConfig, val callbacks: () -> Unit): DashboardComponentEvent
    data class OnNavigateTo(val config: Config): DashboardComponentEvent
    data class OnToggleProductMenu(val isExpanded: Boolean): DashboardComponentEvent
}
