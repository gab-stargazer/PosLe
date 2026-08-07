package org.lelestacia.posle.ui.screen.transaction_history

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
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.toFormattedDate
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_today_data
import posle.shared.generated.resources.label_date_prefix
import posle.shared.generated.resources.label_best_seller
import posle.shared.generated.resources.label_total_transaction_prefix
import posle.shared.generated.resources.label_transaction_count
import posle.shared.generated.resources.label_total_money

@Composable
fun TransactionHistoryScreenHeader(
    state: TransactionHistoryScreenState,
    modifier: Modifier = Modifier
) {
    val totalTransaction = state.todayTransactions
        .size

    Column(
        modifier = modifier
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(Res.string.label_today_data),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        val datePrefix = stringResource(Res.string.label_date_prefix)
        val dateSb = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append(datePrefix)
            }

            withStyle(
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    .toSpanStyle()
            ) {
                append(Clock.System.now().toEpochMilliseconds().toFormattedDate())
            }
        }
        Text(dateSb)

        val bestSellerPrefix = stringResource(Res.string.label_best_seller)
        val mostSaleSb = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append(bestSellerPrefix)
            }

            withStyle(
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    .toSpanStyle()
            ) {
            }
        }
        Text(mostSaleSb)

        val totalTransactionPrefix = stringResource(Res.string.label_total_transaction_prefix)
        val transactionCount = stringResource(Res.string.label_transaction_count, totalTransaction)
        val totalSb = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append(totalTransactionPrefix)
            }

            withStyle(
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    .toSpanStyle()
            ) {
                append(transactionCount)
            }
        }
        Text(totalSb)

        val totalMoney = stringResource(Res.string.label_total_money, "Rp0")
        val totalValueSb = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append(totalMoney)
            }

            withStyle(
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    .toSpanStyle()
            ) {
            }
        }
        Text(totalValueSb)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PreviewTransactionHistoryScreenHeader() {
    AppTheme {
        TransactionHistoryScreenHeader(
            state = TransactionHistoryScreenState(
                todayTransactions = emptyList()
            )
        )
    }
}