package org.lelestacia.posle.screen.transaction_add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IndeterminateCheckBox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.component.TransactionAddComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnAddTransactionClicked
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnTabChanged
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_save_transaction
import posle.shared.generated.resources.label_cart_count
import posle.shared.generated.resources.label_customer_name
import posle.shared.generated.resources.label_product
import posle.shared.generated.resources.label_search_product

@Composable
fun TransactionAddScreen(
    component: TransactionAddComponent,
    modifier: Modifier = Modifier
) {
    val products = component.products.collectAsLazyPagingItems()
    val state by component.state.collectAsStateWithLifecycle()

    TransactionAddUI(
        products = products,
        state = state,
        onEvent = component::onEvent,
        modifier = modifier
    )
}

@Composable
fun TransactionAddUI(
    products: LazyPagingItems<Product>,
    state: TransactionAddState,
    onEvent: (TransactionAddEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(state.searchQuery) {
        snapshotFlow { state.searchQuery.text }
            .collect { query ->
                onEvent(TransactionAddEvent.OnSearchQueryChanged(query.toString()))
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
                    onClick = { onEvent(OnTabChanged(0)) },
                    text = {
                        Text(
                            text = stringResource(Res.string.label_product),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                )

                Tab(
                    selected = state.currentTab == 1,
                    onClick = { onEvent(OnTabChanged(1)) },
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
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.currentTab == 0) {
                TextField(
                    state = state.searchQuery,
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.label_search_product),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            12.dp
                        ),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(25F),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp)
                )

                LazyVerticalGrid(
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1F),
                ) {
                    items(count = products.itemCount, key = products.itemKey { it.id }) {
                        products[it]?.let { product ->
                            ElevatedCard(
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .height(IntrinsicSize.Min)
                                    .clickable(onClick = {
                                        onEvent(
                                            TransactionAddEvent.OnRequestProductConfig(
                                                product
                                            )
                                        )
                                    })
                            ) {
                                Column {
                                    if (product.imageUri != null) {
                                        AsyncImage(
                                            model = product.imageUri,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1F)
                                        )
                                    } else {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1F)
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.IndeterminateCheckBox,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = product.name.value,
                                        style = MaterialTheme.typography.labelLarge,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    Text(
                                        text = "Harga: ${product.sellPrice.value.toRupiah()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1F),
                ) {
                    items(
                        items = state.cartItems,
                    ) { item ->
                        Column(
                            modifier = Modifier.animateItem()
                        ) {
                            TransactionAddItemView(
                                transactionItem = item,
                                onRemove = {
                                    onEvent(TransactionAddEvent.OnRemoveProduct(item))
                                }
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 12.dp)
                        .clip(RoundedCornerShape(25F))
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp))

                ) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Total Harga:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Text(
                            text = state.cartItems.sumOf {

                                val variants =
                                    it.variants.sumOf { variant -> variant.priceAdjustment.value }

                                (variants * it.productAmount.value.toBigDecimal()) + (it.productPrice.value * it.productAmount.value.toBigDecimal())


                            }.toRupiah(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                    }

                    if (state.settings.isCustomerNameNeeded) {
                        TextField(
                            state = state.customerName,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            label = {
                                Text(
                                    text = stringResource(Res.string.label_customer_name),
                                    style = MaterialTheme.typography.labelMediumEmphasized.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(25F),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            onEvent(OnAddTransactionClicked)
                        },
                        shape = RoundedCornerShape(25F),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(stringResource(Res.string.btn_save_transaction))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddUI() {
    AppTheme {

        val products = SampleData.products
        val productsLazyPagingItems =
            MutableStateFlow(PagingData.from(products)).collectAsLazyPagingItems()

        TransactionAddUI(
            products = productsLazyPagingItems,
            state = TransactionAddState(
                currentTab = 1,
                settings = PosLeSettings(
                    isCustomerNameNeeded = true,
                    isProductVolatile = true
                )
            ),
            onEvent = {}
        )
    }
}
