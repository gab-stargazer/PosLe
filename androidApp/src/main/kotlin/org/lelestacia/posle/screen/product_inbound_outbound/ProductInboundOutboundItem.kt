package org.lelestacia.posle.screen.product_inbound_outbound

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Unit
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_inbound
import posle.shared.generated.resources.title_outbound_purchase
import posle.shared.generated.resources.txt_product_inbound
import posle.shared.generated.resources.txt_product_outbound
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ProductInboundOutboundItem(
    stockMovement: StockMovement,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(end = 12.dp)
        ) {
            Text(
                when (stockMovement.movementType) {
                    StockMovementType.Inbound -> "Barang Masuk"
                    StockMovementType.Adjustment, StockMovementType.Purchase -> "Barang Keluar"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                stringResource(
                    when (stockMovement.movementType) {
                        StockMovementType.Inbound -> Res.string.txt_product_inbound
                        StockMovementType.Purchase, StockMovementType.Adjustment -> Res.string.txt_product_outbound
                    },
                    stockMovement.productName.value,
                    abs(stockMovement.amount.value).roundToInt(),
                    stockMovement.productUnit.value
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        Card(
            shape = RoundedCornerShape(25F),
            colors = CardDefaults.cardColors(
                containerColor = when (stockMovement.movementType) {
                    StockMovementType.Inbound -> MaterialTheme.colorScheme.primaryContainer
                    StockMovementType.Purchase -> MaterialTheme.colorScheme.tertiaryContainer
                    StockMovementType.Adjustment -> MaterialTheme.colorScheme.tertiaryContainer
                },
                contentColor =
                    when (stockMovement.movementType) {
                        StockMovementType.Inbound -> MaterialTheme.colorScheme.onPrimaryContainer
                        StockMovementType.Purchase -> MaterialTheme.colorScheme.onTertiaryContainer
                        StockMovementType.Adjustment -> MaterialTheme.colorScheme.onTertiaryContainer
                    }
            )
        ) {
            Text(
                text = stringResource(
                    when (stockMovement.movementType) {
                        StockMovementType.Inbound -> Res.string.title_inbound
                        StockMovementType.Purchase -> Res.string.title_outbound_purchase
                        StockMovementType.Adjustment -> Res.string.title_outbound_purchase
                    }
                ),
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
                movementType = StockMovementType.Inbound,
                amount = Amount(10F),
                createdAt = 0L
            )
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
            )
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
                movementType = StockMovementType.Adjustment,
                amount = Amount(-2F),
                createdAt = 0L
            )
        )
    }
}
