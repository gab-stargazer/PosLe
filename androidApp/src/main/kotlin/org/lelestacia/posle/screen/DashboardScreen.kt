package org.lelestacia.posle.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.DashboardComponent
import org.lelestacia.posle.domain.component.DashboardNavigation
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent.OnNavigateTo
import org.lelestacia.posle.domain.state_event.DashboardComponentState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavDestination
import org.lelestacia.posle.screen.product_list.ProductListScreen
import org.lelestacia.posle.screen.transaction_history.TransactionHistoryScreen
import org.lelestacia.posle.ui.theme.AppTheme

@Composable
fun DashboardScreen(
    component: DashboardComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()

    DashboardUI(
        state = state,
        onEvent = component::onEvent,
        modifier = modifier
    ) { paddingValues ->
        Children(
            stack = component.children,
            animation = stackAnimation(fade()),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val child = it.instance) {
                is NavChild.ProductList -> {
                    ProductListScreen(
                        onNavigateToAddProduct = { addEdit, product ->
                            component.onNavigation(
                                DashboardNavigation.Nav(
                                    Config.ProductAddEdit(
                                        addEdit = addEdit,
                                        product = product
                                    )
                                )
                            )
                        },
                        component = child.component
                    )
                }

                is NavChild.Setting -> {
                    SettingScreen(component = child.component)
                }

                is NavChild.Transaction -> {
                    TransactionHistoryScreen(
                        component = child.component
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardUI(
    state: DashboardComponentState,
    onEvent: (DashboardComponentEvent) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(),
        bottomBar = {
            NavigationBar {
                NavDestination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = state.selectedTab.value == index,
                        onClick = {
                            onEvent(
                                OnNavigateTo(
                                    index = index,
                                    destination = destination.config
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        label = {
                            Text(
                                text = stringResource(destination.title),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        },
        modifier = modifier,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewDashboardUI() {
    AppTheme {
        DashboardUI(
            state = DashboardComponentState(),
            onEvent = {}
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Dashboard Content Placeholder")
            }
        }
    }
}
