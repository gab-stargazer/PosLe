package org.lelestacia.posle.screen.transaction_history.component

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah

@Composable
fun TransactionReceipt(
    storeName: Name,
    customerName: Name,
    transactionDate: Long,
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
            text = storeName.value.ifEmpty { "Posle" }.uppercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        )

        Text(
            text = "Tanggal: ${transactionDate.toFormattedDateTime()}".uppercase(),
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
            cartItems.sellPrice.value * cartItems.quantity.value.toBigDecimal()
//            when (cartItems) {
//                is CartItems.BundleCartItem -> {
//                    cartItems.bundleTotalPrice.value
//                }
//
//                is CartItems.ProductCartItem -> {
//                    cartItems.productSellPrice.value * cartItems.productQuantity.value.toBigDecimal()
//                }
//            }
        }.sumOf { it }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Total Belanja:",
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

    }
}