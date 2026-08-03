package org.lelestacia.posle.ui.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.MintCream
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah
import org.jetbrains.compose.resources.stringResource
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.app_name
import posle.shared.generated.resources.label_receipt_date_format
import posle.shared.generated.resources.label_receiver_format
import posle.shared.generated.resources.label_total_spending
import posle.shared.generated.resources.label_thanks_for_shopping

@Composable
fun TransactionReceipt(
    storeName: Name,
    customerName: Name,
    transactionDate: Long,
    transactionProduct: ImmutableList<TransactionItem>,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RectangleShape,
        colors = CardDefaults.elevatedCardColors(),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(MintCream)
                .padding(24.dp)
        ) {
            val appName = stringResource(Res.string.app_name)
            Text(
                text = storeName.value.ifEmpty { appName }.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )

            Text(
                text = stringResource(Res.string.label_receipt_date_format, transactionDate.toFormattedDateTime()).uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            if (customerName.value.isNotBlank()) {
                Text(
                    stringResource(Res.string.label_receiver_format, customerName.value).uppercase(),
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

            transactionProduct.forEach { transactionItem ->
                when (transactionItem.type) {
                    TransactionItemType.Product -> TransactionReceiptProduct(transactionItem.products.first())
                    TransactionItemType.Bundle -> TransactionReceiptBundle(transactionItem)
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

            val totalPrice = transactionProduct.map { cartItems ->
                cartItems.sellPrice.value * cartItems.quantity.value
            }.sumOf { it }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(Res.string.label_total_spending),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    overflow = TextOverflow.Clip,
                    maxLines = 1
                )

                Text(
                    totalPrice.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    overflow = TextOverflow.Clip,
                    maxLines = 1
                )
            }


            Text(
                stringResource(Res.string.label_thanks_for_shopping),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionReceipt() {
    AppTheme {
        TransactionReceipt(
            storeName = Name("Toko Maju Jaya"),
            customerName = SampleData.sampleTransaction.customerName,
            transactionDate = SampleData.sampleTransaction.createdAt,
            transactionProduct = SampleData.sampleTransaction.items.toImmutableList()
        )
    }
}