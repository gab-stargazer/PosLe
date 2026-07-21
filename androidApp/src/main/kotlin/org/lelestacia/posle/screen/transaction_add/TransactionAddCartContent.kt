package org.lelestacia.posle.screen.transaction_add

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.screen.transaction_add.component.TransactionAddBundleCart
import org.lelestacia.posle.screen.transaction_add.component.TransactionAddProductCart
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_save_transaction
import posle.shared.generated.resources.label_customer_name
import posle.shared.generated.resources.title_cart_is_empty
import posle.shared.generated.resources.txt_total_price

@Composable
fun TransactionAddCartContent(
    cartItems: List<CartItems>,
    customerNameState: TextFieldState,
    isCustomerNameNeeded: Boolean,
    onRemoveItem: (CartItems) -> Unit,
    onSaveTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        AnimatedContent(
            targetState = cartItems.isEmpty(),
            modifier = Modifier.fillMaxSize()
        ) { isEmpty ->
            when (isEmpty) {
                true -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterVertically
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = Icons.Default.ShoppingCart.name,
                            tint = BurgundyRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            stringResource(Res.string.title_cart_is_empty),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BurgundyRed
                            )
                        )
                    }
                }

                false -> {
                    Column {
                        LazyColumn(
                            modifier = Modifier.weight(1F),
                        ) {
                            items(items = cartItems) { item ->
                                Column(
                                    modifier = Modifier.animateItem()
                                ) {
                                    when (item) {
                                        is CartItems.BundleCartItem -> {
                                            Column(
                                                modifier = Modifier.animateItem()
                                            ) {
                                                TransactionAddBundleCart(
                                                    cartItem = item,
                                                    onRemoveFromCart = { onRemoveItem(item) }
                                                )

                                                HorizontalDivider()
                                            }
                                        }

                                        is CartItems.ProductCartItem -> {
                                            Column(modifier = Modifier.animateItem()) {
                                                TransactionAddProductCart(
                                                    cartItem = item,
                                                    onRemoveFromCart = { onRemoveItem(item) }
                                                )
                                                HorizontalDivider()
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        val totalPrice = cartItems
                            .map { cartItem ->
                                when (cartItem) {
                                    is CartItems.BundleCartItem -> {
                                        cartItem.bundleTotalPrice.value * cartItem.bundleQuantity.value.toBigDecimal()
                                    }

                                    is CartItems.ProductCartItem -> {
                                        cartItem.productSellPrice.value * cartItem.productQuantity.value.toBigDecimal()
                                    }
                                }
                            }
                            .sumOf { it }

                        HorizontalDivider()
                        TransactionAddTotalSection(
                            totalPrice = totalPrice.toRupiah(),
                            customerNameState = customerNameState,
                            isCustomerNameNeeded = isCustomerNameNeeded,
                            onSaveTransaction = onSaveTransaction
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionAddTotalSection(
    totalPrice: String,
    customerNameState: TextFieldState,
    isCustomerNameNeeded: Boolean,
    onSaveTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.txt_total_price),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
            )

            Text(
                text = totalPrice,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

        if (isCustomerNameNeeded) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(Util.defaultShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .border(2.dp, BurgundyRed, Util.defaultShape)
            ) {
                TextField(
                    state = customerNameState,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = Icons.Default.Person.name,
                            tint = BurgundyRed
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(Res.string.label_customer_name),
                            style = MaterialTheme.typography.labelMediumEmphasized.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = Util.defaultTransparentTextFieldColor(),
                    shape = Util.defaultShape,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = BurgundyRed
            ),
            onClick = onSaveTransaction,
            shape = RoundedCornerShape(25F),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(stringResource(Res.string.btn_save_transaction))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddCartContent() {

    val customerNameState = remember { TextFieldState("Budi") }

    AppTheme {
        TransactionAddCartContent(
            cartItems = emptyList(),
            customerNameState = customerNameState,
            isCustomerNameNeeded = true,
            onRemoveItem = {},
            onSaveTransaction = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
