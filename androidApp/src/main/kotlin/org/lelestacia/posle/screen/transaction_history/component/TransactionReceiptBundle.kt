package org.lelestacia.posle.screen.transaction_history.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.TransactionProduct
import org.lelestacia.posle.domain.model.groupForDisplay
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_subtotal
import kotlin.time.Clock

@Composable
fun TransactionReceiptBundle(
    transactionItem: TransactionItem,
    modifier: Modifier = Modifier
) {
    val subtotal = transactionItem
        .quantity
        .value * transactionItem.sellPrice.value

    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            Text(
                text = transactionItem.name.value.uppercase() + " x${transactionItem.quantity.value.toDisplayText()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                )
            )

            Text(
                text =
                    when {
                        subtotal.stripTrailingZeros() == transactionItem.sellPrice.value.stripTrailingZeros() ->
                            stringResource(
                                Res.string.title_subtotal,
                                subtotal.toRupiah().uppercase()
                            )

                        else -> transactionItem.sellPrice.value.toRupiah().uppercase()
                    },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                transactionItem.products.groupForDisplay().forEach { product ->
                    Text(
                        text = "- ${product.productName.value} ${product.quantity.value.toDisplayText()} ${product.unit.value}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
        }

        Text(
            text = stringResource(
                Res.string.title_subtotal,
                subtotal.toRupiah().uppercase()
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (transactionItem.note.orEmpty().isNotBlank()) {
            Text(
                "Catatan: ${transactionItem.note}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionReceiptBundle() {
    AppTheme {
        Box(modifier = Modifier.padding(12.dp)) {
            TransactionReceiptBundle(
                transactionItem = TransactionItem(
                    id = 0,
                    type = TransactionItemType.Bundle,
                    referenceId = 0,
                    name = Name("Paket Nasi Ayam"),
                    quantity = Amount(java.math.BigDecimal("10")),
                    sellPrice = Price(13000.toBigDecimal()),
                    note = "Lorem Ipsum",
                    products = List(2) {
                        TransactionProduct(
                            productId = it,
                            productName = Name("Nasi Ayam"),
                            quantity = Amount(java.math.BigDecimal.ONE),
                            unit = Unit("porsi"),
                            skuNumber = null,
                            imageUri = null,
                            buyPrice = Price(10000.toBigDecimal()),
                            sellPrice = Price(12000.toBigDecimal()),
                            note = null,
                            variants = emptyList()
                        )
                    },
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    updatedAt = null
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}