package org.lelestacia.posle.screen.transaction_add.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.ui.theme.successLightHighContrast
import org.lelestacia.posle.util.RupiahVisualTransformation
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toDisplayText
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_to_cart
import posle.shared.generated.resources.btn_cancel
import posle.shared.generated.resources.label_optional_note
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.label_product_available
import posle.shared.generated.resources.label_product_choosen
import posle.shared.generated.resources.label_product_sell_price
import posle.shared.generated.resources.title_add_product
import java.math.BigDecimal
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogProductEvent.OnDismiss as OnDismissDialogProduct
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.DialogProductEvent.OnPriceRequestValidation as OnDialogProductPriceRequestValidation1

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

            BorderedTextField(
                value = state.selectedProduct?.name?.value.orEmpty(),
                onValueChange = {},
                label = stringResource(Res.string.label_product_choosen),
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            BorderedTextField(
                value = state.amount,
                onValueChange = { newAmount ->
                    onEvent(DialogProductEvent.OnAmountChanged(newAmount))
                },
                label = stringResource(Res.string.label_product_amount),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                suffix = {
                    if (state.settings.isProductStockTracked) {
                        val stock =
                            (state.selectedProduct?.stock?.value ?: BigDecimal.ZERO).toDisplayText()
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
                keyboardActions = KeyboardActions(
                    onDone = {
                        onEvent(DialogProductEvent.OnPriceRequestValidation)
                        focusManager.clearFocus(true)
                    }
                ),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = state.isAmountValidated,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = successLightHighContrast
                        )
                    }
                },
                errorMessage = state.amountError,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (state.settings.isProductVolatile) {
                BorderedTextField(
                    value = state.price,
                    onValueChange = { newPrice ->
                        onEvent(DialogProductEvent.OnPriceChanged(newPrice))
                    },
                    label = stringResource(Res.string.label_product_sell_price),
                    visualTransformation = RupiahVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onEvent(OnDialogProductPriceRequestValidation1)
                            focusManager.clearFocus(true)
                        }
                    ),
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = state.isPriceValidated,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = successLightHighContrast
                            )
                        }
                    },
                    errorMessage = state.priceError,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            BorderedTextField(
                state = state.noteState,
                label = stringResource(Res.string.label_optional_note),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = Icons.Default.EditNote.name,
                        tint = CharcoalBlue
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    focusManager.clearFocus(true)
                },
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(top = 12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onEvent(OnDismissDialogProduct)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BurgundyRed
                    ),
                    border = BorderStroke(1.dp, BurgundyRed),
                    shape = Util.defaultShape,
                    modifier = Modifier.weight(1F)
                ) {
                    Text(
                        stringResource(Res.string.btn_cancel),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
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
                        .weight(1f)
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