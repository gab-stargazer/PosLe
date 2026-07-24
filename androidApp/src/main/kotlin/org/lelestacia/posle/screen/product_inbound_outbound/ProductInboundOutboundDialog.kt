package org.lelestacia.posle.screen.product_inbound_outbound

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnAddMovementClicked
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnMovementTypeChanged
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnProductNameChanged
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnProductSelected
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentState.ProductInboundOutboundAddStockState
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.ui.theme.successLight
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.Util
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_mutation
import posle.shared.generated.resources.label_movement_type
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.label_product_name
import posle.shared.generated.resources.title_add_stock_movement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInboundOutboundDialog(
    state: ProductInboundOutboundAddStockState,
    onEvent: (ProductInboundOutboundAddStockEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            var isExpanded by remember { mutableStateOf(false) }
            Text(
                stringResource(Res.string.title_add_stock_movement),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .border(
                        width = 1.dp,
                        color = CharcoalBlue,
                        shape = Util.defaultShape
                    )
            ) {
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = {
                        isExpanded = it
                    }
                ) {
                    TextField(
                        value = state.productName.value,
                        onValueChange = { newName ->
                            onEvent(OnProductNameChanged(Name(newName)))
                        },
                        shape = Util.defaultShape,
                        colors = Util.defaultTransparentTextFieldColor(),
                        label = {
                            Text(
                                text = stringResource(Res.string.label_product_name),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        trailingIcon = {
                            this@Column.AnimatedVisibility(
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
            }

            var isStockMovementTypeExpanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .border(
                        width = 1.dp,
                        color = CharcoalBlue,
                        shape = Util.defaultShape
                    )
            ) {
                ExposedDropdownMenuBox(
                    expanded = isStockMovementTypeExpanded,
                    onExpandedChange = {
                        isStockMovementTypeExpanded = it
                    }
                ) {
                    TextField(
                        value = stringResource(state.selectedMovementType.title),
                        onValueChange = { newName ->
                            onEvent(OnProductNameChanged(Name(newName)))
                        },
                        shape = Util.defaultShape,
                        colors = Util.defaultTransparentTextFieldColor(),
                        label = {
                            Text(
                                text = stringResource(Res.string.label_movement_type),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(isStockMovementTypeExpanded)
                        },
                        readOnly = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = isStockMovementTypeExpanded,
                        onDismissRequest = {
                            isStockMovementTypeExpanded = false
                        }
                    ) {
                        StockMovementType.entries.filterNot { it == StockMovementType.Sale }
                            .forEach { movementType ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(movementType.title),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    onClick = {
                                        onEvent(OnMovementTypeChanged(movementType))
                                        isStockMovementTypeExpanded = false
                                    }
                                )
                            }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = Icons.Default.Info.name,
                    tint = BurgundyRed
                )

                Text(
                    stringResource(state.selectedMovementType.description),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            BorderedTextField(
                state = state.amountAdded,
                label = stringResource(Res.string.label_product_amount),
                labelStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onEvent(OnAddMovementClicked) },
                enabled = state.selectedProduct != null && state.amountAdded.text.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(Res.string.btn_add_mutation),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewInboundOutboundDialog() {
    AppTheme {
        ProductInboundOutboundDialog(
            state = ProductInboundOutboundAddStockState(
                availableProducts = SampleData.products
            ),
            onEvent = {

            },
            modifier = Modifier.padding(12.dp)
        )
    }
}