package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardStateEvent
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavConfig
import org.lelestacia.posle.util.SelectedTabIndex

class DashboardComponent(
    componentContext: ComponentContext,
    val children: Value<ChildStack<NavConfig, NavChild>>,
    private val onNavigateTo: (NavConfig) -> Unit,
    val onNavigateToAddEditProduct: (AddEdit, Product?) -> Unit
) : ComponentContext by componentContext {

    val scope = CoroutineScope(Dispatchers.Main.immediate)

    val state: Value<DashboardStateEvent>
        field = MutableValue(DashboardStateEvent())

    fun onEvent(event: DashboardComponentEvent) = scope.launch {
        when (event) {
            is DashboardComponentEvent.OnNavigateTo -> {
                state.update { currentState ->
                    currentState.copy(
                        selectedTab = SelectedTabIndex(event.index)
                    )
                }

                onNavigateTo(event.destination)
            }
        }
    }
}

