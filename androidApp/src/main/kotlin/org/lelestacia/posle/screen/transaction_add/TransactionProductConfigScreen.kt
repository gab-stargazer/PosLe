package org.lelestacia.posle.screen.transaction_add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.TransactionProductConfigComponent
import org.lelestacia.posle.domain.component.TransactionProductConfigEvent
import org.lelestacia.posle.domain.component.TransactionProductConfigEvent.OnVariantClicked
import org.lelestacia.posle.domain.component.TransactionProductConfigState
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.screen.product_add.VariantViewItemAdd
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.RupiahOutputTransformation
import org.lelestacia.posle.util.Unit
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_to_cart
import posle.shared.generated.resources.label_config_product_desc
import posle.shared.generated.resources.label_optional_note
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.label_product_price_latest
import java.math.BigDecimal

@Composable
fun TransactionProductConfigScreen(
    component: TransactionProductConfigComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = state.product.name.value,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(Res.string.label_config_product_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (state.product.variants.isNotEmpty()) {
                    item {
                        Text(
                            text = "Variasi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(state.product.variants) { variant ->
                        VariantViewItemAdd(
                            variant = variant,
                            isSelected = variant in state.selectedVariants,
                            isEnableContextMenu = false,
                            onCheckedChange = { variant, isChecked ->
                                component.onEvent(OnVariantClicked(variant, isChecked))
                            },
                            onEdit = {},
                            onDelete = {}
                        )
                        HorizontalDivider()
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {


                        if (state.settings.isAmountPrecise) {
                            TextField(
                                state = state.amountState,
                                label = {
                                    Text(
                                        stringResource(Res.string.label_product_amount),
                                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent
                                ),
                                suffix = {
                                    Text(state.product.unit.value)
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                onKeyboardAction = { focusManager.clearFocus() },
                                shape = RoundedCornerShape(25F),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    stringResource(Res.string.label_product_amount),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        val current =
                                            state.amountState.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO
                                        if (current > BigDecimal.ONE) component.onEvent(
                                            TransactionProductConfigEvent.OnAmountChanged(current.subtract(
                                                BigDecimal.ONE))
                                        )
                                    }) {
                                        Icon(Icons.Default.Remove, contentDescription = null)
                                    }
                                    Text(
                                        text = state.amountState.text.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                    IconButton(onClick = {
                                        val current =
                                            state.amountState.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO
                                        component.onEvent(
                                            TransactionProductConfigEvent.OnAmountChanged(
                                                current.add(BigDecimal.ONE)
                                            )
                                        )
                                    }) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                    }
                                }
                            }
                        }

                        if (state.settings.isProductVolatile) {
                            TextField(
                                state = state.priceState,
                                label = {
                                    Text(
                                        stringResource(Res.string.label_product_price_latest),
                                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                },
                                outputTransformation = RupiahOutputTransformation(),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                onKeyboardAction = { focusManager.clearFocus() },
                                shape = RoundedCornerShape(25F),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        TextField(
                            state = state.noteState,
                            label = {
                                Text(
                                    stringResource(Res.string.label_optional_note),
                                    style = MaterialTheme.typography.labelMediumEmphasized.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            onKeyboardAction = { focusManager.clearFocus() },
                            shape = RoundedCornerShape(25F),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Button(
                onClick = { component.onEvent(TransactionProductConfigEvent.OnConfirmed) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(25f)
            ) {
                Text(stringResource(Res.string.btn_add_to_cart))
            }
        }
    }
}

private class TransactionProductConfigComponentPreview(
    initialState: TransactionProductConfigState
) : TransactionProductConfigComponent {

    override val state: Value<TransactionProductConfigState> = MutableValue(initialState)

    override fun onEvent(event: TransactionProductConfigEvent) {

    }
}

@Preview
@Composable
private fun TransactionProductConfigScreenPreview() {
    val previewProduct = Product(
        id = 1,
        name = Name("Es Teh Manis"),
        buyPrice = Price(BigDecimal("15000")),
        sellPrice = Price(BigDecimal("15000")),
        unit = Unit("Cup"),
        stock = Amount(BigDecimal("5")),
        variants = listOf(
            Variant(
                id = 1,
                name = Name("Less Sugar"),
                priceAdjustment = Price(BigDecimal.ZERO)
            ),
            Variant(
                id = 2,
                name = Name("Extra Ice"),
                priceAdjustment = Price(BigDecimal.ZERO)
            ),
        )
    )

    AppTheme {
        TransactionProductConfigScreen(
            component = TransactionProductConfigComponentPreview(
                initialState = TransactionProductConfigState(
                    product = previewProduct,
                    priceState = TextFieldState(previewProduct.sellPrice.value.toString())
                )
            )
        )
    }
}