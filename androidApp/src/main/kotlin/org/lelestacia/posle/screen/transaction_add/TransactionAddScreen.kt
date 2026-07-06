package org.lelestacia.posle.screen.transaction_add

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.transaction_add.TransactionAddComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogEvent.OnDismiss
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogEvent.OnShown
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnAddTransactionClicked
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnNavigateToQrScanner
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnRemoveProduct
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnTabChanged
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.SampleData
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_cart_count
import posle.shared.generated.resources.label_product

@Composable
fun TransactionAddScreen(
    component: TransactionAddComponent,
    modifier: Modifier = Modifier
) {
    val products = component.products.collectAsLazyPagingItems()
    val state by component.state.collectAsStateWithLifecycle()
    val cameraPermission = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(android.Manifest.permission.CAMERA, "", isRequired = true)
        )
    )

    LaunchedEffect(state.searchQuery) {
        snapshotFlow { state.searchQuery.text }
            .collect { query ->
                component.onEvent(TransactionAddEvent.OnSearchQueryChanged(query.toString()))
            }
    }

    if (state.isDialogShown) {
        Dialog(
            onDismissRequest = {
                component.onEvent(OnDismiss)
            }
        ) {
            TransactionAddProductDialog(
                state = state.dialogState,
                onEvent = component::onEvent
            )
        }
    }

    Scaffold(
        topBar = {
            PrimaryTabRow(
                selectedTabIndex = state.currentTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = state.currentTab == 0,
                    onClick = { component.onEvent(OnTabChanged(0)) },
                    text = {
                        Text(
                            text = stringResource(Res.string.label_product),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                )

                Tab(
                    selected = state.currentTab == 1,
                    onClick = { component.onEvent(OnTabChanged(1)) },
                    text = {
                        Text(
                            text = stringResource(
                                Res.string.label_cart_count,
                                state.cartItems.size
                            ),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(state.currentTab == 0) {
                FloatingActionButton(
                    onClick = {
                        if (cameraPermission.allRequiredGranted()) {
                            component.onEvent(OnNavigateToQrScanner)
                        } else {
                            cameraPermission.requestPermission()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = Icons.Default.QrCodeScanner.name
                    )
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(state.currentTab == 0) { isProductTab ->
                when (isProductTab) {
                    true -> {
                        Column {
                            TransactionAddSearchBar(
                                state = state.searchQuery,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .padding(top = 12.dp)
                            )

                            TransactionAddProductGrid(
                                products = products,
                                onProductClick = { product ->
                                    component.onEvent(OnShown(product))
                                },
                                modifier = Modifier.weight(1F)
                            )
                        }
                    }

                    false -> {
                        TransactionAddCartContent(
                            cartItems = state.cartItems,
                            customerNameState = state.customerName,
                            isCustomerNameNeeded = state.settings.isCustomerNameNeeded,
                            onRemoveItem = { item ->
                                component.onEvent(OnRemoveProduct(item))
                            },
                            onSaveTransaction = {
                                component.onEvent(OnAddTransactionClicked)
                            },
                            modifier = Modifier.weight(1F)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddScreen() {
    AppTheme {

        val products = SampleData.products

        TransactionAddScreen(
            component = object : TransactionAddComponent {
                override val products: Flow<PagingData<Product>>
                    get() = flowOf(PagingData.from(products))

                override val state: StateFlow<TransactionAddState>
                    get() = MutableStateFlow(TransactionAddState())

                override fun onEvent(event: TransactionAddEvent) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}
