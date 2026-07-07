package org.lelestacia.posle.screen.transaction_recap.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.txt_total_product_outbound
import posle.shared.generated.resources.txt_total_profit

@Composable
fun TransactionRecapProductOutbound(
    transactionItems: List<TransactionItem>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productName = transactionItems
        .first()
        .productName
        .value

    val amountOut = transactionItems
        .sumOf { it.productAmount.value.toBigDecimal() }
        .toFloat()
        .toDisplayText()

    val totalProfit = transactionItems
        .sumOf { it.productAmount.value.toBigDecimal() * (it.productSellPrice.value - it.productBuyPrice.value) }
        .toRupiah()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column {
                Column {
                    Text(
                        productName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        stringResource(Res.string.txt_total_product_outbound, amountOut),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        stringResource(Res.string.txt_total_profit, totalProfit),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = Icons.AutoMirrored.Filled.ArrowRight.name
            )
        }

        HorizontalDivider()
    }
}