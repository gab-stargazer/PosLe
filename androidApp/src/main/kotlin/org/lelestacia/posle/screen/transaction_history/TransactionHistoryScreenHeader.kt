package org.lelestacia.posle.screen.transaction_history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.domain.component.TransactionHistoryScreenState
import org.lelestacia.posle.util.toFormattedDate
import org.lelestacia.posle.util.toRupiah
import kotlin.time.Clock

@Composable
fun TransactionHistoryScreenHeader(
    state: TransactionHistoryScreenState,
    modifier: Modifier = Modifier
) {
    val totalTransaction = state.todayTransactions
        .size

    val totalTransactionValue = state.todayTransactions
        .sumOf {
            it.items.sumOf { item -> item.productAmount.value.toBigDecimal() * item.productPrice.value }
        }

    val mostSoldItemToday =
        state.todayTransactions
            .flatMap { it.items }
            .groupingBy { it.productName }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

    Column(
        modifier = modifier
            .padding(12.dp)
    ) {
        Text(
            text = "Data hari ini:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        val dateSb = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append("Tanggal: ")
            }

            withStyle(
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    .toSpanStyle()
            ) {
                append(Clock.System.now().toEpochMilliseconds().toFormattedDate())
            }
        }
        Text(dateSb)

        val mostSaleSb = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append("Item Terlaris: ")
            }

            withStyle(
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    .toSpanStyle()
            ) {
                append(mostSoldItemToday?.value ?: "Belum ada data")
            }
        }
        Text(mostSaleSb)

        val totalSb = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append("Total Transaksi: ")
            }

            withStyle(
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    .toSpanStyle()
            ) {
                append("$totalTransaction Transaksi")
            }
        }
        Text(totalSb)

        val totalValueSb = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append("Total Uang: ")
            }

            withStyle(
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    .toSpanStyle()
            ) {
                append(totalTransactionValue.toRupiah())
            }
        }
        Text(totalValueSb)
    }
}