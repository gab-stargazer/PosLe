package org.lelestacia.posle.ui.screen.product_inbound_outbound

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.skydoves.compose.stability.runtime.TraceRecomposition
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponent
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnToggleDialog
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toDisplayText
import java.math.BigDecimal
import androidx.compose.ui.tooling.preview.Preview
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_mutation
import posle.shared.generated.resources.label_unit_format

@TraceRecomposition
@Composable
fun ProductInboundOutboundScreen(
    component: ProductInboundOutboundComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()
    val paging = component.priceMovement.collectAsLazyPagingItems()
    val products = component.products.collectAsLazyPagingItems()


    if (state.isAddStockShown) {
        Dialog(
            onDismissRequest = { component.onEvent(OnToggleDialog) }
        ) {
            ProductInboundOutboundDialog(
                state = state.addMovementState,
                onEvent = component::onEvent
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                state.settingState.isProductStockTracked,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { component.onEvent(OnToggleDialog) }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Text(stringResource(Res.string.btn_add_mutation))
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                bottom = 128.dp,
                start = 12.dp,
                end = 12.dp,
                top = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            items(
                count = paging.itemCount,
                key = paging.itemKey { stockMovement -> stockMovement.id }) {
                paging[it]?.let { stockMovement ->
                    Column(modifier = Modifier.animateItem()) {
                        ProductInboundOutboundItem(
                            stockMovement = stockMovement,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductStockItem(
    product: Product,
    modifier: Modifier = Modifier
) {
    val amount = product.stock.value.toDisplayText()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.weight(1F)) {
            Text(
                text = product.name.value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = stringResource(Res.string.label_unit_format, product.unit.value),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = amount,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PreviewProductInboundOutboundScreen() {
    org.lelestacia.posle.ui.theme.AppTheme {
        ProductInboundOutboundScreen(
            component = object : ProductInboundOutboundComponent {
                override val priceMovement = kotlinx.coroutines.flow.flowOf(androidx.paging.PagingData.from(emptyList<org.lelestacia.posle.domain.model.StockMovement>()))
                override val products = kotlinx.coroutines.flow.flowOf(androidx.paging.PagingData.from(emptyList<org.lelestacia.posle.domain.model.Product>()))
                override val state = kotlinx.coroutines.flow.MutableStateFlow(org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentState())
                override fun onEvent(event: org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentEvent) {}
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductStockItem() {
    org.lelestacia.posle.ui.theme.AppTheme {
        ProductStockItem(
            product = Product(
                id = "0",
                name = Name("Salak Pondoh"),
                stock = Amount(BigDecimal("50")),
                unit = org.lelestacia.posle.util.Unit(value = "Kg"),
                buyPrice = Price(BigDecimal("10000")),
                sellPrice = Price(BigDecimal("15000"))
            )
        )
    }
}
