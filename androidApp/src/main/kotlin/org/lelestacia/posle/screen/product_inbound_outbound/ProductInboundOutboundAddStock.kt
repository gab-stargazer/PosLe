package org.lelestacia.posle.screen.product_inbound_outbound

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnAddStockClicked
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnProductNameChanged
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnProductSelected
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentState.ProductInboundOutboundAddStockState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.successLight
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.SampleData
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.label_product_name
import posle.shared.generated.resources.title_add_product_stock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInboundOutboundAddStockDialog(
    state: ProductInboundOutboundAddStockState,
    onEvent: (ProductInboundOutboundAddStockEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            var isExpanded by remember { mutableStateOf(false) }
            Text(
                stringResource(Res.string.title_add_product_stock),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = {
                    isExpanded = it
                },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                TextField(
                    value = state.productName.value,
                    onValueChange = { newName ->
                        onEvent(OnProductNameChanged(Name(newName)))
                    },
                    shape = RoundedCornerShape(25F),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    label = {
                        Text(
                            text = stringResource(Res.string.label_product_name),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            state.selectedProduct != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = Icons.Default.Check.name,
                                tint = successLight
                            )
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = {
                        isExpanded = false
                    }
                ) {
                    state.availableProducts.forEach { product ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = product.name.value,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onEvent(OnProductSelected(product))
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                state = state.amountAdded,
                shape = RoundedCornerShape(25F),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                label = {
                    Text(
                        text = stringResource(Res.string.label_product_amount),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onEvent(OnAddStockClicked) },
                enabled = state.selectedProduct != null && state.amountAdded.text.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Tambah Stok")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewInboundOutboundAddStockDialog() {
    AppTheme {
        ProductInboundOutboundAddStockDialog(
            state = ProductInboundOutboundAddStockState(
                availableProducts = SampleData.products
            ),
            onEvent = {

            },
            modifier = Modifier.padding(12.dp)
        )
    }
}