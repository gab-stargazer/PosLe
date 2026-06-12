package org.lelestacia.posle.screen.transaction_add

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.App
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.label_product_price_latest
import java.math.BigDecimal
import org.lelestacia.posle.util.Unit as CustomUnit

@Composable
fun TransactionAddItem(
    product: Product,
    productMap: Map<Product, TransactionItemState>,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(
                topStart = 25F,
                topEnd = 25F,
                bottomStart = 25F
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = when (product.imageUri != null) {
                            true -> 0.dp
                            false -> 12.dp
                        }, end = 12.dp
                    )
                    .height(IntrinsicSize.Min)
            ) {

                if (product.imageUri != null) {
                    AsyncImage(
                        model = product.imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(96.dp)
                            .aspectRatio(1F)
                            .padding(all = 6.dp)
                            .clip(RoundedCornerShape(25F))
                    )
                }

                Column(
                    horizontalAlignment =
                        when (product.imageUri != null) {
                            true -> Alignment.End
                            false -> Alignment.Start
                        },
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 12.dp)
                        .padding(end = 12.dp)
                ) {
                    Text(product.name.value, style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${product.price.value.toRupiah()}/${product.unit.value}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                IconButton(
                    onClick = {
                        when (product in productMap) {
                            true -> onRemove()
                            false -> onAdd()
                        }
                    }
                ) {
                    AnimatedContent(product in productMap) { isInMap ->
                        when (isInMap) {
                            true -> {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = null
                                )
                            }

                            false -> {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            product in productMap,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    state = productMap[product]?.amountState ?: rememberTextFieldState(),
                    label = {
                        Text(
                            stringResource(Res.string.label_product_amount),
                            style = MaterialTheme.typography.labelMediumEmphasized
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(25F)
                )

                if (product.isProductVolatile) {
                    TextField(
                        state = productMap[product]?.priceState ?: rememberTextFieldState(),
                        label = {
                            Text(
                                stringResource(Res.string.label_product_price_latest),
                                style = MaterialTheme.typography.labelMediumEmphasized
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(25F),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddItem() {
    App {
        TransactionAddItem(
            product = Product(
                id = 0,
                name = Name("Salak"),
                unit = CustomUnit("Kg"),
                price = Price(BigDecimal("100000")),
                isProductVolatile = true
            ),
            productMap = mapOf(
                Product(
                    id = 0,
                    name = Name("Salak"),
                    unit = CustomUnit("Kg"),
                    price = Price(BigDecimal("100000")),
                    isProductVolatile = true
                ) to TransactionItemState()
            ),
            onAdd = {

            },
            onRemove = {

            },
            modifier = Modifier.padding(12.dp)
        )
    }
}