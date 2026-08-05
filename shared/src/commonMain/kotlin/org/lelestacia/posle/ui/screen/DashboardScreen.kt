package org.lelestacia.posle.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.lelestacia.posle.domain.component.dashboard.DashboardComponent
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardComponentEvent
import org.lelestacia.posle.domain.state_event.DashboardComponentState
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.NavChild
import org.lelestacia.posle.navigation.NavConfig
import org.lelestacia.posle.navigation.NavDestination
import org.lelestacia.posle.ui.platform.PlatformBackHandler
import org.lelestacia.posle.ui.platform.rememberCameraPermissionState
import org.lelestacia.posle.ui.platform.rememberExportNotifier
import org.lelestacia.posle.ui.platform.rememberImportNotifier
import org.lelestacia.posle.ui.platform.rememberNotificationPermissionState
import org.lelestacia.posle.ui.screen.analytics.AnalyticsScreen
import org.lelestacia.posle.ui.screen.product_inbound_outbound.ProductInboundOutboundScreen
import org.lelestacia.posle.ui.screen.product_list.ProductListScreen
import org.lelestacia.posle.ui.screen.transaction_add.TransactionAddScreen
import org.lelestacia.posle.ui.screen.transaction_history.TransactionHistoryScreen
import org.lelestacia.posle.ui.screen.transaction_recap.TransactionRecapScreen
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.util.FileStorage
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.action_export_products
import posle.shared.generated.resources.action_import_products
import posle.shared.generated.resources.btn_allow
import posle.shared.generated.resources.btn_later
import posle.shared.generated.resources.cd_print_recap
import posle.shared.generated.resources.cd_product_menu
import posle.shared.generated.resources.cd_search_transaction
import posle.shared.generated.resources.dialog_notification_permission_desc
import posle.shared.generated.resources.dialog_notification_permission_title
import posle.shared.generated.resources.label_menu
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    component: DashboardComponent,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val state by component.state.collectAsStateWithLifecycle()
    val cameraPermission = rememberCameraPermissionState()
    val fileStorage = koinInject<FileStorage>()
    val notifyExport = rememberExportNotifier()
    val notifyImport = rememberImportNotifier()
    val notificationPermission = rememberNotificationPermissionState()

    var showNotificationRationale by remember { mutableStateOf(false) }
    var pendingProductAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(pendingProductAction, notificationPermission.isGranted.value) {
        if (pendingProductAction != null && notificationPermission.isGranted.value) {
            val action = pendingProductAction!!
            pendingProductAction = null
            action()
        }
    }

    if (showNotificationRationale) {
        AlertDialog(
            onDismissRequest = {
                showNotificationRationale = false
                pendingProductAction = null
            },
            title = { Text(stringResource(Res.string.dialog_notification_permission_title)) },
            text = { Text(stringResource(Res.string.dialog_notification_permission_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    showNotificationRationale = false
                    notificationPermission.requestPermission()
                }) {
                    Text(stringResource(Res.string.btn_allow))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNotificationRationale = false
                    pendingProductAction = null
                }) {
                    Text(stringResource(Res.string.btn_later))
                }
            }
        )
    }

    val runIfNotificationGranted: (() -> Unit) -> Unit = { action ->
        if (notificationPermission.isGranted.value) {
            action()
        } else {
            pendingProductAction = action
            showNotificationRationale = true
        }
    }

    val activeChild = component.children.active.instance

    val productImportLauncher = rememberFilePickerLauncher { file ->
        if (activeChild is NavChild.ProductList) {
            scope.launch {
                val bytes = file?.readBytes() ?: return@launch
                activeChild.component.onEvent(
                    ProductListComponentEvent.OnImportProducts(bytes) { success ->
                        if (success) notifyImport()
                    }
                )
            }
        }
    }

    PlatformBackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        scrimColor = Color.Black.copy(0.5F),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
                windowInsets = WindowInsets()
            ) {
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
                        shape = RoundedCornerShape(topEnd = 25F, bottomEnd = 25F),
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
                                            delay(100.milliseconds)
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
                            selectedContainerColor = BurgundyRed,
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
                val activeChild = component.children.active.instance
                TopAppBar(
                    title = {
                        Text(
                            stringResource(
                                NavDestination.entries[state.selectedTab.value].title
                            ),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    actions = {
                        AnimatedVisibility(
                            activeChild is NavChild.ProductList
                        ) {
                            Box {
                                IconButton(
                                    onClick = {
                                        component.onEvent(DashboardComponentEvent.OnToggleProductMenu(true))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = stringResource(Res.string.cd_product_menu)
                                    )
                                }

                                DropdownMenu(
                                    expanded = state.isProductMenuExpanded,
                                    onDismissRequest = {
                                        component.onEvent(DashboardComponentEvent.OnToggleProductMenu(false))
                                    }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(Res.string.action_export_products)) },
                                        onClick = {
                                            component.onEvent(DashboardComponentEvent.OnToggleProductMenu(false))
                                            runIfNotificationGranted {
                                                (activeChild as NavChild.ProductList).component
                                                    .onEvent(
                                                        ProductListComponentEvent.OnExportProducts { bytes ->
                                                            val savedPath = fileStorage.saveToPublicDocuments(
                                                                fileName = "products.xlsx",
                                                                subFolder = "Daftar Produk",
                                                                data = bytes
                                                            )
                                                            notifyExport("products.xlsx", savedPath)
                                                        }
                                                    )
                                            }
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text(stringResource(Res.string.action_import_products)) },
                                        onClick = {
                                            component.onEvent(DashboardComponentEvent.OnToggleProductMenu(false))
                                            runIfNotificationGranted {
                                                productImportLauncher.launch()
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            activeChild is NavChild.TransactionRecap
                        ) {
                            IconButton(
                                onClick = {
                                    (activeChild as NavChild.TransactionRecap).component
                                        .onEvent(
                                            TransactionRecapEvent.OnPrintRecap
                                        )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = stringResource(Res.string.cd_print_recap)
                                )
                            }
                        }

                        AnimatedVisibility(
                            activeChild is NavChild.TransactionHistory || activeChild is NavChild.TransactionRecap
                        ) {
                            IconButton(
                                onClick = {
                                    component.onEvent(
                                        DashboardComponentEvent.OnNavigateTo(Config.TransactionSearch)
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(Res.string.cd_search_transaction)
                                )
                            }
                        }
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
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

                    is NavChild.Analytics -> {
                        AnalyticsScreen(component = child.component)
                    }

                    is NavChild.Setting -> {
                        SettingScreen(component = child.component)
                    }

                    is NavChild.TransactionAdd -> {
                        TransactionAddScreen(
                            component = child.component,
                            isCameraPermissionGranted = cameraPermission.isGranted.value,
                            onRequestCameraPermission = cameraPermission::requestPermission
                        )
                    }

                    is NavChild.TransactionRecap -> {
                        TransactionRecapScreen(component = child.component)
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

@Preview(showBackground = true)
@Composable
private fun PreviewDashboardScreen() {
    AppTheme {
        DashboardScreen(
            component = object : DashboardComponent {
                override val children: Value<ChildStack<NavConfig, NavChild>>
                    get() = MutableValue(
                        ChildStack(
                            configuration = NavConfig.TransactionAdd,
                            instance = NavChild.TransactionAdd(
                                component = object : org.lelestacia.posle.domain.component.transaction_add.TransactionAddComponent {
                                    override val bundles = kotlinx.coroutines.flow.flowOf(androidx.paging.PagingData.from(emptyList<org.lelestacia.posle.domain.model.Bundle>()))
                                    override val products = kotlinx.coroutines.flow.flowOf(androidx.paging.PagingData.from(emptyList<org.lelestacia.posle.domain.model.Product>()))
                                    override val state = MutableStateFlow(org.lelestacia.posle.domain.state_event.TransactionAddState())
                                    override fun onEvent(event: org.lelestacia.posle.domain.state_event.TransactionAddEvent) {}
                                }
                            )
                        )
                    )
                override val state: StateFlow<DashboardComponentState>
                    get() = MutableStateFlow(DashboardComponentState())

                override fun onEvent(event: DashboardComponentEvent) {}
            }
        )
    }
}
