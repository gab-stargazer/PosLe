package org.lelestacia.posle.ui.screen.transaction_add.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.BundleProduct
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_subtotal
import posle.shared.generated.resources.txt_bundle_content
import posle.shared.generated.resources.txt_bundle_content_detail
import posle.shared.generated.resources.txt_cart_item_note
import java.math.BigDecimal
import org.lelestacia.posle.util.Unit as PosleUnit

@Composable
fun TransactionAddBundleCart(
    cartItem: CartItems.BundleCartItem,
    onRemoveFromCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bundleProducts = cartItem.bundleProducts
    var isShownDeleteButton by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val backgroundColor by animateColorAsState(
        targetValue =
            when (isShownDeleteButton) {
                true -> MaterialTheme.colorScheme.surfaceContainerHighest
                false -> Color.Transparent
            },
        label = "background color animation"
    )

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .indication(
                interactionSource = interactionSource,
                indication = ripple()
            )
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        val press = PressInteraction.Press(it)
                        interactionSource.emit(press)

                        val released = tryAwaitRelease()

                        interactionSource.emit(
                            if (released) {
                                PressInteraction.Release(press)
                            } else {
                                PressInteraction.Cancel(press)
                            }
                        )
                    },
                    onLongPress = {
                        isShownDeleteButton = true
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(all = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    cartItem.bundleName.value + " x" + cartItem.bundleQuantity.value.toDisplayText(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    val totalPrice =
                        cartItem.bundleTotalPrice.value * cartItem.bundleQuantity.value

                    Text(
                        when {
                            totalPrice.stripTrailingZeros() == cartItem.bundleTotalPrice.value.stripTrailingZeros() -> {
                                stringResource(
                                    Res.string.title_subtotal,
                                    totalPrice.toRupiah()
                                )
                            }

                            else -> {
                                cartItem.bundleTotalPrice.value.toRupiah()
                            }
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    if (totalPrice.stripTrailingZeros() != cartItem.bundleTotalPrice.value.stripTrailingZeros()) {
                        Text(
                            stringResource(Res.string.title_subtotal, totalPrice.toRupiah()),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Text(
                stringResource(Res.string.txt_bundle_content),
                style = MaterialTheme.typography.bodyMedium
            )

            bundleProducts.forEach { product ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = Icons.Default.Remove.name,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        stringResource(
                            Res.string.txt_bundle_content_detail,
                            product.productName.value,
                            product.quantity.value.toDisplayText(),
                            product.unit.value
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (!cartItem.bundleNote.isNullOrBlank()) {
                Text(
                    text = stringResource(
                        Res.string.txt_cart_item_note,
                        cartItem.bundleNote.orEmpty()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = isShownDeleteButton,
            enter = expandHorizontally(
                expandFrom = Alignment.End
            ),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.End
            ),
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(64.dp)
                        .weight(1F)
                        .background(BurgundyRed)
                        .clickable(onClick = onRemoveFromCart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = Icons.Default.Delete.name,
                        tint = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(64.dp)
                        .weight(1F)
                        .background(CharcoalBlue)
                        .clickable(
                            onClick = {
                                isShownDeleteButton = false
                            }
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = Icons.Default.Close.name,
                        tint = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddBundleCart() {
    AppTheme {
        TransactionAddBundleCart(
            cartItem = CartItems.BundleCartItem(
                id = 0,
                bundleId = 0,
                bundleName = Name("Paket Kombo"),
                bundleQuantity = Amount(BigDecimal("3")),
                bundleTotalPrice = Price(BigDecimal("12000")),
                bundleNote = "Lorem ipsum dolor sit amet",
                bundleProducts = listOf(
                    BundleProduct(
                        productId = 0,
                        productName = Name("Nasi"),
                        skuNumber = null,
                        imageUri = null,
                        quantity = Amount(java.math.BigDecimal.ONE),
                        buyPrice = Price(BigDecimal("4000")),
                        sellPrice = Price(BigDecimal("4000")),
                        sellPriceIndividual = Price(7000.toBigDecimal()),
                        unit = PosleUnit("Pcs"),
                        createdAt = 0L,
                        updatedAt = null
                    ),
                    BundleProduct(
                        productId = 0,
                        productName = Name("Ayam"),
                        skuNumber = null,
                        imageUri = null,
                        quantity = Amount(java.math.BigDecimal.ONE),
                        buyPrice = Price(BigDecimal("8000")),
                        sellPrice = Price(BigDecimal("8000")),
                        sellPriceIndividual = Price(7000.toBigDecimal()),
                        createdAt = 0L,
                        unit = PosleUnit("Pcs"),
                        updatedAt = null
                    )
                )
            ),
            onRemoveFromCart = {},
            modifier = Modifier
        )
    }
}
