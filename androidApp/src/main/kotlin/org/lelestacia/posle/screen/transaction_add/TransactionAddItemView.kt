package org.lelestacia.posle.screen.transaction_add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_variant
import java.math.BigDecimal
import kotlin.math.roundToInt
import org.lelestacia.posle.util.Unit as PosLeUnit

@Composable
fun TransactionAddItemView(
    transactionItem: TransactionItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val amount =
        if (transactionItem.productAmount.value % 1 == 0F) {
            transactionItem.productAmount.value.roundToInt()
        } else {
            transactionItem.productAmount.value
        }

    Column(
        modifier = modifier
            .padding(start = 12.dp)
            .padding(vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = transactionItem.productName.value,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1F)
                    )

                    Text(
                        text = transactionItem.productSellPrice.value.toRupiah(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$amount ${transactionItem.productUnit.value}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 12.dp)
                    )

                    Text(
                        text = (transactionItem.productSellPrice.value * transactionItem.productAmount.value.toBigDecimal()).toRupiah(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = null
                )
            }
        }

        if (transactionItem.variants.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.label_variant),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        transactionItem.variants.forEach { variant ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth(0.85F)
                    .padding(start = 12.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.SubdirectoryArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        "${variant.name.value} x $amount",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    variant.priceAdjustment.value.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.85F)
                .padding(top = 6.dp)
        ) {
            Text(
                text = "Subtotal:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            val sumVariant =
                transactionItem.variants.sumOf { it.priceAdjustment.value * transactionItem.productAmount.value.toBigDecimal() }
            val sumItem =
                transactionItem.productAmount.value.toBigDecimal() * transactionItem.productSellPrice.value

            Text(
                (sumVariant + sumItem).toRupiah(),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (transactionItem.productNote.orEmpty().isNotBlank()) {
            Text(
                "Catatan:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(transactionItem.productNote.orEmpty(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionItem() {
    AppTheme {
        TransactionAddItemView(
            transactionItem = TransactionItem(
                id = 0,
                productName = Name("Salak Pondoh"),
                productId = 1,
                productBuyPrice = Price(BigDecimal.ZERO),
                productSellPrice = Price(BigDecimal(15000)),
                productUnit = PosLeUnit("Kg"),
                productAmount = Amount(50F),
                productNote = "1 Karung",
                variants = listOf(
                    Variant(
                        id = 0,
                        name = Name("Karung"),
                        priceAdjustment = Price(BigDecimal("5000"))
                    )
                )
            ),
            onRemove = {},
            modifier = Modifier
        )
    }
}