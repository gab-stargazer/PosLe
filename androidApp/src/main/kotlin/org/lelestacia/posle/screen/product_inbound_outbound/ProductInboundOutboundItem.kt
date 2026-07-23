package org.lelestacia.posle.screen.product_inbound_outbound

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toFormattedDateTime
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.txt_product_inbound
import posle.shared.generated.resources.txt_product_outbound
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ProductInboundOutboundItem(
    stockMovement: StockMovement,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = Util.defaultShape,
        modifier = modifier
            .border(1.dp, CharcoalBlue, Util.defaultShape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = when (stockMovement.movementType) {
                        StockMovementType.Purchase, StockMovementType.AdjustmentIncrease -> "Barang Masuk"
                        StockMovementType.AdjustmentDecrease, StockMovementType.Sale, StockMovementType.Return -> "Barang Keluar"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    "Waktu: ${stockMovement.createdAt.toFormattedDateTime()}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    stringResource(
                        when (stockMovement.movementType) {
                            StockMovementType.Purchase, StockMovementType.AdjustmentIncrease -> Res.string.txt_product_inbound
                            StockMovementType.Sale, StockMovementType.AdjustmentDecrease, StockMovementType.Return -> Res.string.txt_product_outbound
                        },
                        stockMovement.productName.value,
                        abs(stockMovement.amount.value).roundToInt(),
                        stockMovement.productUnit.value
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(end = 12.dp, top = 6.dp)
                )
            }

            Card(
                shape = Util.defaultShape,
                colors = CardDefaults.cardColors(
                    containerColor = stockMovement.movementType.backgroundColor,
                    contentColor = stockMovement.movementType.textColor
                )
            ) {
                Text(
                    text = stringResource(stockMovement.movementType.title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductInboundItem() {
    AppTheme {
        ProductInboundOutboundItem(
            stockMovement = StockMovement(
                id = 1,
                productId = 1,
                productName = Name("Sate Ayam"),
                productUnit = Unit("Porsi"),
                movementType = StockMovementType.Sale,
                amount = Amount(10F),
                createdAt = 0L
            ),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductOutboundItem() {
    AppTheme {
        ProductInboundOutboundItem(
            stockMovement = StockMovement(
                id = 2,
                productId = 2,
                productName = Name("Es Teh Manis"),
                productUnit = Unit("Gelas"),
                movementType = StockMovementType.Purchase,
                amount = Amount(-5F),
                createdAt = 0L
            ),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductAdjustmentItem() {
    AppTheme {
        ProductInboundOutboundItem(
            stockMovement = StockMovement(
                id = 3,
                productId = 3,
                productName = Name("Nasi Putih"),
                productUnit = Unit("Porsi"),
                movementType = StockMovementType.AdjustmentDecrease,
                amount = Amount(-2F),
                createdAt = 0L
            ),
            modifier = Modifier.padding(12.dp)
        )
    }
}
