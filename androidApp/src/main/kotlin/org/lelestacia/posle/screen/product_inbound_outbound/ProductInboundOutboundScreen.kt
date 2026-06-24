package org.lelestacia.posle.screen.product_inbound_outbound

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponent
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnToggleDialog
import org.lelestacia.posle.domain.model.Product
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_product
import kotlin.math.roundToInt

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
            ProductInboundOutboundAddStockDialog(
                state = state.addStockState,
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
                        Text(stringResource(Res.string.btn_add_product))
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(
                paging.itemCount,
                paging.itemKey { stockMovement -> stockMovement.id }) {
                paging[it]?.let { stockMovement ->
                    Column(modifier = Modifier.animateItem()) {
                        ProductInboundOutboundItem(
                            stockMovement = stockMovement,
                            modifier = Modifier
                        )
                        HorizontalDivider()
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
    val amount =
        if (product.stock.value % 1 == 0F) {
            product.stock.value.roundToInt()
        } else {
            product.stock.value
        }

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
                text = "Satuan: ${product.unit.value}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = amount.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}
