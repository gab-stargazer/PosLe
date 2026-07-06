package org.lelestacia.posle.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.dashboard.DashboardComponent
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavDestination
import org.lelestacia.posle.screen.product_inbound_outbound.ProductInboundOutboundScreen
import org.lelestacia.posle.screen.product_list.ProductListScreen
import org.lelestacia.posle.screen.transaction_add.TransactionAddScreen
import org.lelestacia.posle.screen.transaction_history.TransactionHistoryScreen
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_menu

@Composable
fun DashboardScreen(
    component: DashboardComponent,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val state by component.state.collectAsStateWithLifecycle()

    BackHandler(drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = stringResource(Res.string.label_menu),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(24.dp)
                )
                Spacer(Modifier.height(12.dp))
                NavDestination.entries.forEachIndexed { index, destination ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(destination.title),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        },
                        selected = state.selectedTab.value == index,
                        onClick = {
                            component.onEvent(
                                DashboardComponentEvent.OnMenuNavigateTo(
                                    index = index,
                                    destination = destination.config,
                                    callbacks = {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                )
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = stringResource(destination.title)
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(
                                NavDestination.entries[state.selectedTab.value].title
                            ),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets(),

            modifier = modifier,
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
                        ProductListScreen(component = child.component)
                    }

                    is NavChild.Setting -> {
                        SettingScreen(component = child.component)
                    }

                    is NavChild.TransactionAdd -> {
                        TransactionAddScreen(component = child.component)
                    }

                    is NavChild.TransactionHistory -> {
                        TransactionHistoryScreen(component = child.component)
                    }

                    is NavChild.ProductInboundOutbound -> {
                        ProductInboundOutboundScreen(component = child.component)
                    }
                }
            }
        }
    }
}
