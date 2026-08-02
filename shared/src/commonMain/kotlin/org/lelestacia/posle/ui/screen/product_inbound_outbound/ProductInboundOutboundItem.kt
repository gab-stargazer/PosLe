package org.lelestacia.posle.ui.screen.product_inbound_outbound

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toFormattedDateTime
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_product_movement_adjustment_decrease
import posle.shared.generated.resources.title_product_movement_adjustment_increase
import posle.shared.generated.resources.title_product_movement_purchase
import posle.shared.generated.resources.title_product_movement_return
import posle.shared.generated.resources.title_product_movement_sale
import java.math.BigDecimal

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
            .shadow(
                elevation = 4.dp,
                shape = Util.defaultShape,
                clip = true,
            )
            .border(1.dp, BurgundyRed, Util.defaultShape)
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
                        fontWeight = FontWeight.Bold,
                        color = BurgundyRed
                    )
                )

                Text(
                    "Waktu: ${stockMovement.createdAt.toFormattedDateTime()}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = buildAnnotatedString {
                        val raw = stringResource(
                            when (stockMovement.movementType) {
                                StockMovementType.Purchase -> Res.string.title_product_movement_purchase
                                StockMovementType.Sale -> Res.string.title_product_movement_sale
                                StockMovementType.Return -> Res.string.title_product_movement_return
                                StockMovementType.AdjustmentIncrease -> Res.string.title_product_movement_adjustment_increase
                                StockMovementType.AdjustmentDecrease -> Res.string.title_product_movement_adjustment_decrease
                            },
                            stockMovement.productName.value,
                            stockMovement.amount.value.abs().toDisplayText(),
                            stockMovement.productUnit.value
                        )
                        // Render <b>...</b> segments as bold.
                        val segments = raw.split("<b>", "</b>")
                        segments.forEachIndexed { index, segment ->
                            if (segment.isEmpty()) return@forEachIndexed
                            if (index % 2 == 1) {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(segment)
                                }
                            } else {
                                append(segment)
                            }
                        }
                    },
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
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
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
                amount = Amount(BigDecimal("10")),
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
                amount = Amount(BigDecimal("-5")),
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
                amount = Amount(BigDecimal("-2")),
                createdAt = 0L
            ),
            modifier = Modifier.padding(12.dp)
        )
    }
}
