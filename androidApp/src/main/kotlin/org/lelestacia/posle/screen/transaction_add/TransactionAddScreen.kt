package org.lelestacia.posle.screen.transaction_add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogProductEvent.OnDismiss
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogProductEvent.OnShown
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnAddTransactionClicked
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnNavigateToQrScanner
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnRemoveProduct
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnTabChanged
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.screen.transaction_add.component.TransactionAddBundle
import org.lelestacia.posle.screen.transaction_add.component.TransactionAddBundleDialog
import org.lelestacia.posle.screen.transaction_add.component.TransactionAddProductDialog
import org.lelestacia.posle.screen.transaction_add.component.TransactionAddSearchBar
import org.lelestacia.posle.screen.transaction_add.component.TransactionAddTitle
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.util.SampleData
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_cart_count
import posle.shared.generated.resources.label_product
import posle.shared.generated.resources.title_bundle
import posle.shared.generated.resources.title_product
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogBundleEvent.OnAddToCartClicked as OnAddBundleToCartClicked
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogBundleEvent.OnDismiss as OnDismissDialogBundle
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogBundleEvent.OnQuantityChanged as OnDialogBundleQuantityChanged
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogBundleEvent.OnQuantityValidationRequest as OnDialogBundleQuantityValidationRequest
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogBundleEvent.OnShown as OnDialogBundleShown

@Composable
fun TransactionAddScreen(
    component: TransactionAddComponent,
    modifier: Modifier = Modifier
) {
    val bundles = component.bundles.collectAsLazyPagingItems()
    val products = component.products.collectAsLazyPagingItems()
    val state by component.state.collectAsStateWithLifecycle()
    val cameraPermission = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(android.Manifest.permission.CAMERA, "", isRequired = true)
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )

    LaunchedEffect(state.currentTab) {
        if (state.currentTab != pagerState.currentPage) {
            pagerState.animateScrollToPage(state.currentTab)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        component.onEvent(OnTabChanged(pagerState.currentPage))
    }

    if (state.isDialogBundleShown) {
        Dialog(
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            ),
            onDismissRequest = {
                component.onEvent(OnDismissDialogBundle)
            }
        ) {
            TransactionAddBundleDialog(
                state = state.dialogBundleState,
                onQuantityChange = { newQuantity ->
                    component.onEvent(OnDialogBundleQuantityChanged(newQuantity))
                },
                onQuantityValidationRequest = {
                    component.onEvent(OnDialogBundleQuantityValidationRequest)
                },
                onCancel = {
                    component.onEvent(OnDismissDialogBundle)
                },
                onAddToCart = {
                    component.onEvent(OnAddBundleToCartClicked)
                }
            )
        }
    }

    if (state.isDialogProductShown) {
        Dialog(
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
            ),
            onDismissRequest = {
                component.onEvent(OnDismiss)
            }
        ) {
            TransactionAddProductDialog(
                state = state.dialogProductState,
                onEvent = component::onEvent
            )
        }
    }

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(paddingValues)
        ) {
            PrimaryTabRow(
                selectedTabIndex = state.currentTab,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex = state.currentTab),
                        color = BurgundyRed
                    )
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selectedContentColor = BurgundyRed,
                    unselectedContentColor = CharcoalBlue,
                    selected = state.currentTab == 0,
                    onClick = { component.onEvent(OnTabChanged(0)) },
                    text = {
                        Text(
                            text = stringResource(Res.string.label_product),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                )

                Tab(
                    selectedContentColor = BurgundyRed,
                    unselectedContentColor = CharcoalBlue,
                    selected = state.currentTab == 1,
                    onClick = { component.onEvent(OnTabChanged(1)) },
                    text = {
                        Text(
                            text = stringResource(
                                Res.string.label_cart_count,
                                state.cartItems.size
                            ),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                )
            }

            HorizontalPager(
                state = pagerState
            ) { page ->
                when (page) {
                    0 -> {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(horizontal = 12.dp)
                                    .padding(top = 12.dp)
                            ) {
                                TransactionAddSearchBar(
                                    searchQuery = state.searchQuery,
                                    onSearchQueryChange = {
                                        component.onEvent(
                                            TransactionAddEvent.OnSearchQueryChanged(
                                                it
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1F)
                                        .padding(end = 12.dp)
                                )

                                Button(
                                    shape = RoundedCornerShape(25F),
                                    onClick = {
                                        if (cameraPermission.allRequiredGranted()) {
                                            component.onEvent(OnNavigateToQrScanner)
                                        } else {
                                            cameraPermission.requestPermission()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BurgundyRed,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .scale(1F)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = Icons.Default.QrCodeScanner.name
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (bundles.itemCount > 0) {
                                Column(
                                    modifier = Modifier
                                        .weight(1F)

                                ) {
                                    TransactionAddTitle(
                                        Res.string.title_bundle,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )

                                    LazyHorizontalGrid(
                                        rows = GridCells.Fixed(2),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        contentPadding = PaddingValues(6.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        items(bundles.itemCount) { index ->
                                            bundles[index]?.let { bundle ->
                                                TransactionAddBundle(
                                                    bundle,
                                                    onClick = {
                                                        component.onEvent(
                                                            OnDialogBundleShown(
                                                                selectedBundle = bundle
                                                            )
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .width(LocalWindowInfo.current.containerDpSize.width - 24.dp)
                                                        .animateItem()
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1F)
                            ) {
                                TransactionAddTitle(
                                    Res.string.title_product,
                                    modifier = Modifier.padding(end = 12.dp)
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
                    }

                    1 -> {
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
                override val bundles: Flow<PagingData<Bundle>>
                    get() = flowOf()

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
