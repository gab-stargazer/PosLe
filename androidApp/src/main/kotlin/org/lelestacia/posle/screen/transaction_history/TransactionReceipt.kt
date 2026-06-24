package org.lelestacia.posle.screen.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah
import java.math.BigDecimal
import kotlin.math.roundToInt
import kotlin.time.Clock

@Composable
fun TransactionReceipt(
    storeName: Name,
    customerName: Name,
    transactionProduct: ImmutableList<TransactionItem>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text(
            storeName.value.uppercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        )

        Text(
            "Tanggal: ${Clock.System.now().toEpochMilliseconds().toFormattedDateTime()}".uppercase(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        if (customerName.value.isNotBlank()) {
            Text(
                "Penerima: ${customerName.value}".uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Text(
            "================================================================================================",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            overflow = TextOverflow.Clip,
            maxLines = 1
        )

        transactionProduct.forEach { product ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = product.productName.value.uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )

                Text(
                    text = product.productSellPrice.value.toRupiah().uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val value = if (product.productAmount.value % 1F == 0F) {
                    product.productAmount.value.roundToInt().toString()
                } else {
                    product.productAmount.value.toString()
                }

                Text(
                    text = "\t$value ${product.productUnit.value}".uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.weight(1F)
                )

                val subtotal = product
                    .productAmount
                    .value
                    .toBigDecimal() * product.productSellPrice.value

                Text(
                    text = subtotal.toRupiah().uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier.weight(2F)
                )
            }
        }

        Text(
            "================================================================================================",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            overflow = TextOverflow.Clip,
            maxLines = 1
        )

        Text(
            "Terimakasih telah berbelanja",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionReceipt() {
    AppTheme {
        TransactionReceipt(
            storeName = Name("Suisei Salak"),
            customerName = Name("Kaori Cicak"),
            transactionProduct = List(10) {
                TransactionItem(
                    id = it,
                    productId = it,
                    productName = Name("Produk $it"),
                    productBuyPrice = Price(BigDecimal.ZERO),
                    productSellPrice = Price(it.toBigDecimal() * BigDecimal(1000)),
                    productUnit = Unit("Pcs"),
                    productNote = "Lorem Ipsum",
                    productAmount = Amount(10F),
                    variants = emptyList()
                )
            }.toImmutableList()
        )
    }
}