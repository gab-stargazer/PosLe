package org.lelestacia.posle.ui.screen.bundle_add_edit.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.RupiahVisualTransformation
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.digitsOnly
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.label_product_sell_price_promo
import posle.shared.generated.resources.msg_error_quantity_cannot_be_empty
import posle.shared.generated.resources.msg_error_something_went_wrong
import org.lelestacia.posle.util.Unit as PosleUnit

@Composable
fun BundlePriceAndQty(
    productUnit: PosleUnit,
    quantity: String,
    quantityError: StringResource?,
    onQuantityChange: (String) -> Unit,
    price: String,
    priceError: StringResource?,
    onPriceChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardManager = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()

    val quantityTooltipState = rememberTooltipState()
    val priceTooltipState = rememberTooltipState()

    val borderColor by animateColorAsState(
        targetValue =
            if (quantityError != null) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface,
        label = "QuantityBorderColor"
    )

    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(
                    topStart = 25F,
                    topEnd = 25F,
                    bottomStart = 0F,
                    bottomEnd = 0F
                )
            )
        ) {
            TextField(
                value = quantity,
                onValueChange = { newQuantity ->
                    onQuantityChange(newQuantity.digitsOnly())
                },
                label = {
                    Text(
                        text = stringResource(resource = Res.string.label_product_amount),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                suffix = {
                    Text(
                        text = productUnit.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                trailingIcon = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Above
                        ),
                        tooltip = {
                            RichTooltip {
                                Text(
                                    text = stringResource(
                                        resource = quantityError ?: Res.string.msg_error_something_went_wrong
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                        state = quantityTooltipState
                    ) {
                        this@Column.AnimatedVisibility(
                            quantityError != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                onClick = {
                                    keyboardManager?.hide()
                                    focusManager.clearFocus(true)
                                    scope.launch { quantityTooltipState.show(mutatePriority = MutatePriority.PreventUserInput) }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = Icons.Default.ErrorOutline.name
                                )
                            }
                        }
                    }
                },
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
                isError = quantityError != null,
                colors = Util.defaultTransparentTextFieldColor(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier
                .offset(y = (-1).dp)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    RoundedCornerShape(
                        bottomStart = 25F,
                        bottomEnd = 25F,
                        topStart = 0F,
                        topEnd = 0F
                    )
                )
        ) {
            TextField(
                value = price,
                onValueChange = { newPrice ->
                    onPriceChange(newPrice.digitsOnly())
                },
                label = {
                    Text(
                        text = stringResource(resource = Res.string.label_product_sell_price_promo),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                trailingIcon = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Above
                        ),
                        tooltip = {
                            RichTooltip {
                                Text(
                                    text = stringResource(
                                        resource = priceError ?: Res.string.msg_error_something_went_wrong
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                        state = priceTooltipState
                    ) {
                        this@Column.AnimatedVisibility(
                            priceError != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        keyboardManager?.hide()
                                        focusManager.clearFocus(true)
                                        priceTooltipState.show(mutatePriority = MutatePriority.PreventUserInput)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = Icons.Default.ErrorOutline.name
                                )
                            }
                        }
                    }
                },
                isError = priceError != null,
                visualTransformation = RupiahVisualTransformation(),
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
                colors = Util.defaultTransparentTextFieldColor(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBundlePriceAndQuantity() {
    AppTheme {
        BundlePriceAndQty(
            productUnit = PosleUnit("Pcs"),
            quantity = "10",
            quantityError = Res.string.msg_error_quantity_cannot_be_empty,
            onQuantityChange = {},
            price = "10000",
            priceError = null,
            onPriceChange = {},
            modifier = Modifier.padding(24.dp)
        )
    }
}