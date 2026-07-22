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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.navigation.Config.TransactionRecapProductItem
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_view_detail
import posle.shared.generated.resources.txt_total_product_outbound
import posle.shared.generated.resources.txt_total_profit

@Composable
fun TransactionRecapProductOutbound(
    transactionProducts: List<TransactionRecapProductItem>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productName = transactionProducts
        .first()
        .product
        .productName

    val amountOut = transactionProducts
        .sumOf { it.product.quantity.value.toBigDecimal() }
        .toFloat()
        .toDisplayText()

    val totalProfit = transactionProducts
        .sumOf { it.product.quantity.value.toBigDecimal() * (it.product.sellPrice.value - it.product.buyPrice.value) }
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
                .padding(start = 12.dp)
                .padding(vertical = 12.dp)
        ) {
            Column {
                Column {
                    Text(
                        productName.value,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = BurgundyRed
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

            TextButton(
                onClick = onClick
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        stringResource(Res.string.btn_view_detail),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BurgundyRed
                        )
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowRight.name,
                        tint = BurgundyRed
                    )
                }
            }
        }

        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionRecapProductOutbound() {
    AppTheme {
//        TransactionRecapProductOutbound(
//            SampleData.sampleTransaction.items.flatMap { it.products }
//                .filter { it.productName.value == "Kopi Kapal Api Sachet" },
//            {}
//        )
    }
}