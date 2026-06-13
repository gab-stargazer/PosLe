package org.lelestacia.posle.screen.transaction_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.App
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(vertical = 6.dp)
        ) {
            if (transaction.customerName.value.isNotBlank()) {
                Text(
                    text = "Nama Pelanggan\t: ${transaction.customerName.value}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "Total Belanja\t: ${
                    transaction
                        .items
                        .sumOf { it.productAmount.value.toBigDecimal() * it.productPrice.value }
                        .toRupiah()
                }",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Waktu Transaksi: ${transaction.createdAt.toFormattedDateTime()}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowRight,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun PreviewTransactionItem() {
    App {
        TransactionItem(
            transaction = Transaction(
                id = 0,
                customerName = Name("Syidik"),
                items =
                    SampleData.products.map {
                        TransactionItem(
                            id = it.id,
                            productName = it.name,
                            productPrice = it.price,
                            productUnit = it.unit,
                            productAmount = Amount(1F)
                        )
                    },
                createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
            ),
            onClick = {},
            modifier = Modifier
        )
    }
}