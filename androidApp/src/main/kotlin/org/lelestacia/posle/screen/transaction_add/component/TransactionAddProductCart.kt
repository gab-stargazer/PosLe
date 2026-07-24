package org.lelestacia.posle.screen.transaction_add.component

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
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
import posle.shared.generated.resources.txt_cart_item_amount
import posle.shared.generated.resources.txt_cart_item_note
import org.lelestacia.posle.util.Unit as PosleUnit

@Composable
fun TransactionAddProductCart(
    cartItem: CartItems.ProductCartItem,
    onRemoveFromCart: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            .fillMaxWidth()
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
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = cartItem.productName.value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    val totalPrice =
                        cartItem.productSellPrice.value *
                                cartItem.productQuantity.value.toBigDecimal()

                    Text(
                        when {
                            totalPrice.stripTrailingZeros() == cartItem.productSellPrice.value.stripTrailingZeros() -> {
                                stringResource(
                                    Res.string.title_subtotal,
                                    totalPrice.toRupiah()
                                )
                            }

                            else -> {
                                cartItem.productSellPrice.value.toRupiah()
                            }
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    if (totalPrice.stripTrailingZeros() != cartItem.productSellPrice.value.stripTrailingZeros()) {
                        Text(
                            stringResource(Res.string.title_subtotal, totalPrice.toRupiah()),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            val amountSb = buildAnnotatedString {
                withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    append(stringResource(Res.string.txt_cart_item_amount))
                }
                withStyle(
                    MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        .toSpanStyle()
                ) {
                    append("${cartItem.productQuantity.value.toDisplayText()} ${cartItem.productUnit.value}")
                }
            }

            Text(amountSb)

            if (!cartItem.productNote.isNullOrBlank()) {
                Text(
                    text = stringResource(
                        Res.string.txt_cart_item_note,
                        cartItem.productNote.orEmpty()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
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
private fun PreviewTransactionAddProductCart() {
    AppTheme {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TransactionAddProductCart(
                cartItem = CartItems.ProductCartItem(
                    id = 0,
                    productId = 0,
                    productName = Name("Sate Ayam"),
                    skuNumber = null,
                    imageUri = null,
                    productQuantity = Amount(5F),
                    productBuyPrice = Price(10000.toBigDecimal()),
                    productSellPrice = Price(10000.toBigDecimal()),
                    productUnit = PosleUnit("Porsi"),
                    productNote = "Lorem Ipsum"
                ),
                onRemoveFromCart = {},
            )
        }
    }
}
