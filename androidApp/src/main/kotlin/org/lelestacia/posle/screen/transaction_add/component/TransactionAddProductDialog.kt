package org.lelestacia.posle.screen.transaction_add.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogProductEvent
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.util.RupiahVisualTransformation
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toDisplayText
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_to_cart
import posle.shared.generated.resources.label_optional_note
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.label_product_available
import posle.shared.generated.resources.label_product_choosen
import posle.shared.generated.resources.label_product_sell_price
import posle.shared.generated.resources.title_add_product

@Composable
fun TransactionAddProductDialog(
    state: TransactionAddState.DialogProductState,
    onEvent: (DialogProductEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    ElevatedCard(
        shape = Util.defaultShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                stringResource(Res.string.title_add_product),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Box(
                modifier = Modifier
                    .border(2.dp, CharcoalBlue, Util.defaultShape)
            ) {
                TextField(
                    value = state.selectedProduct?.name?.value.orEmpty(),
                    onValueChange = {},
                    label = {
                        Text(
                            stringResource(Res.string.label_product_choosen),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalBlue
                            )
                        )
                    },
                    colors = Util.defaultTransparentTextFieldColor(),
                    shape = Util.defaultShape,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .border(2.dp, CharcoalBlue, Util.defaultShape)
            ) {
                TextField(
                    value = state.amount,
                    onValueChange = { newAmount ->
                        onEvent(DialogProductEvent.OnAmountChanged(newAmount))
                    },
                    label = {
                        Text(
                            stringResource(Res.string.label_product_amount),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalBlue
                            )
                        )
                    },
                    suffix = {
                        if (state.settings.isProductStockTracked) {
                            val stock = (state.selectedProduct?.stock?.value ?: 0F).toDisplayText()
                            val unit = state.selectedProduct?.unit?.value.toString()

                            Text(
                                text = stringResource(
                                    Res.string.label_product_available,
                                    stock,
                                    unit
                                ),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    },
                    colors = Util.defaultTransparentTextFieldColor(),
                    shape = Util.defaultShape,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus(true)
                        }
                    ),
                    isError = state.amountError != null,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = state.amountError != null,
                enter = expandVertically(),
                exit = shrinkVertically(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 3.dp)
            ) {
                state.amountError?.let { error ->
                    Text(
                        text = stringResource(resource = error),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }

            if (state.settings.isProductVolatile) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .border(2.dp, CharcoalBlue, Util.defaultShape)
                ) {
                    TextField(
                        value = state.price,
                        onValueChange = { newPrice ->
                            onEvent(DialogProductEvent.OnPriceChanged(newPrice))
                        },
                        label = {
                            Text(
                                stringResource(Res.string.label_product_sell_price),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalBlue
                                )
                            )
                        },
                        visualTransformation = RupiahVisualTransformation(),
                        colors = Util.defaultTransparentTextFieldColor(),
                        shape = Util.defaultShape,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus(true)
                            }
                        ),
                        isError = state.priceStateError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            AnimatedVisibility(
                visible = state.priceStateError != null,
                enter = expandVertically(),
                exit = shrinkVertically(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 3.dp)
            ) {
                state.priceStateError?.let { error ->
                    Text(
                        text = stringResource(resource = error),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .border(2.dp, CharcoalBlue, Util.defaultShape)
            ) {
                TextField(
                    state = state.noteState,
                    label = {
                        Text(
                            stringResource(Res.string.label_optional_note),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalBlue
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = Icons.Default.EditNote.name,
                            tint = CharcoalBlue
                        )
                    },
                    colors = Util.defaultTransparentTextFieldColor(),
                    shape = Util.defaultShape,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        focusManager.clearFocus(true)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    onEvent(DialogProductEvent.OnAddClicked)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurgundyRed
                ),
                shape = Util.defaultShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text(
                    stringResource(Res.string.btn_add_to_cart),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTransactionAddProductDialog() {
    AppTheme {
        TransactionAddProductDialog(
            state = TransactionAddState.DialogProductState(
                selectedProduct = SampleData.products.first(),
                settings = PosLeSettings(
                    isProductVolatile = true
                )
            ),
            onEvent = {}
        )
    }
}