package org.lelestacia.posle.screen.transaction_add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.MutableStateFlow
import org.lelestacia.posle.App
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.component.TransactionAddComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnAddTransactionClicked
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.domain.state_event.TransactionItemState

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
    Scaffold(modifier = modifier) { paddingValues ->
        Column {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .weight(1F)
                    .padding(paddingValues),
            ) {
                items(count = products.itemCount, key = products.itemKey { it.id }) {
                    products[it]?.let { product ->
                        TransactionAddItem(
                            product = product,
                            productMap = state.products,
                            settings = state.settings,
                            onAdd = {
                                onEvent(TransactionAddEvent.OnAddNewProduct(product))
                            },
                            onRemove = {
                                onEvent(TransactionAddEvent.OnRemoveProduct(product))
                            },
                            onAmountChanged = { newAmount ->
                                onEvent(TransactionAddEvent.OnAmountChanged(product, newAmount))
                            },
                            modifier = Modifier.animateItem()
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
                                text = "Nama Pelanggan",
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
                            .padding(top = 12.dp)
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
                    Text("Simpan Transaksi")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddUI() {
    App {
        val products = org.lelestacia.posle.util.SampleData.products
        val productsLazyPagingItems =
            MutableStateFlow(PagingData.from(products)).collectAsLazyPagingItems()

        TransactionAddUI(
            products = productsLazyPagingItems,
            state = TransactionAddState(
                products = mapOf(
                    products[0] to TransactionItemState(
                        amountState = TextFieldState("2"),
                        priceState = TextFieldState("15000")
                    ),
                    products[1] to TransactionItemState(
                        amountState = TextFieldState("1"),
                        priceState = TextFieldState("5000")
                    )
                ),
                settings = PosLeSettings(
                    isCustomerNameNeeded = true
                )
            ),
            onEvent = {}
        )
    }
}
