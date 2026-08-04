package org.lelestacia.posle.ui.screen.transaction_recap_product_view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.TransactionProduct
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.Cerulean
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import org.jetbrains.compose.resources.stringResource
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_sold
import posle.shared.generated.resources.label_modal
import posle.shared.generated.resources.label_sell
import posle.shared.generated.resources.label_margin
import posle.shared.generated.resources.label_product_badge
import posle.shared.generated.resources.label_bundle_badge

@Composable
fun TransactionProductDetailItem(
    type: TransactionItemType,
    product: TransactionProduct,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1F)
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.label_sold),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(Res.string.label_modal),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(Res.string.label_sell),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(Res.string.label_margin),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column {
                    Text(
                        "${product.quantity.value.toDisplayText()} ${product.unit.value}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = product.buyPrice.value.toRupiah(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = product.sellPrice.value.toRupiah(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = ((product.sellPrice.value - product.buyPrice.value) * product.quantity.value).toRupiah(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            OutlinedCard(
                border = BorderStroke(
                    width = 2.dp,
                    color = when (type) {
                        TransactionItemType.Product -> BurgundyRed
                        TransactionItemType.Bundle -> Cerulean
                    }
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.Transparent,
                ),
                shape = Util.defaultShape
            ) {
                Text(
                    when (type) {
                        TransactionItemType.Product -> stringResource(Res.string.label_product_badge)
                        TransactionItemType.Bundle -> stringResource(Res.string.label_bundle_badge)
                    },
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (type) {
                            TransactionItemType.Product -> BurgundyRed
                            TransactionItemType.Bundle -> Cerulean
                        }
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                )
            }
        }


        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionProductDetailItem() {
    AppTheme {
        TransactionProductDetailItem(
            type = TransactionItemType.Bundle,
            product = TransactionProduct(
                productId = "0",
                productName = Name("Nasi Goreng"),
                skuNumber = null,
                imageUri = null,
                buyPrice = Price(5000.toBigDecimal()),
                sellPrice = Price(6000.toBigDecimal()),
                unit = Unit("Pcs"),
                note = null,
                quantity = Amount(java.math.BigDecimal("5")),
                variants = emptyList()
            )
        )
    }
}