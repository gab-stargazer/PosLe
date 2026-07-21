package org.lelestacia.posle.screen.transaction_add.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.model.BundleProduct
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.txt_bundle_content
import posle.shared.generated.resources.txt_bundle_content_more
import posle.shared.generated.resources.txt_bundle_product
import java.math.BigDecimal
import kotlin.time.Clock
import org.lelestacia.posle.util.Unit as PosleUnit

@Composable
fun TransactionAddBundle(
    bundle: Bundle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .border(2.dp, CharcoalBlue, RoundedCornerShape(25F))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = bundle.name.value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    bundle.bundleProducts.sumOf { it.sellPrice.value * it.quantity.value.toBigDecimal() }
                        .toRupiah(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Text(
                text = stringResource(Res.string.txt_bundle_content),
                style = MaterialTheme.typography.bodyMedium
            )

            val firstTwoContent = bundle.bundleProducts.take(2)

            firstTwoContent.forEach { bundleProduct ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = Icons.Default.SubdirectoryArrowRight.name,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = stringResource(
                                Res.string.txt_bundle_product,
                                bundleProduct.productName.value,
                                bundleProduct.quantity.value.toDisplayText(),
                                bundleProduct.unit.value
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (bundle.bundleProducts.size > 2) {
                Text(
                    text = stringResource(
                        Res.string.txt_bundle_content_more,
                        bundle.bundleProducts.size - 2
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddBundle() {
    AppTheme {
        TransactionAddBundle(
            Bundle(
                id = 0,
                name = Name("Paket Kombo"),
                imageUri = null,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = null,
                bundleProducts = listOf(
                    BundleProduct(
                        productId = 0,
                        productName = Name("Nasi"),
                        skuNumber = null,
                        imageUri = null,
                        quantity = Amount(1F),
                        buyPrice = Price(BigDecimal(3000)),
                        sellPrice = Price(BigDecimal("4000")),
                        unit = PosleUnit("Pcs"),
                        createdAt = Clock.System.now().toEpochMilliseconds(),
                        updatedAt = null
                    ),
                    BundleProduct(
                        productId = 0,
                        productName = Name("Ayam"),
                        skuNumber = null,
                        imageUri = null,
                        quantity = Amount(1F),
                        buyPrice = Price(BigDecimal(3000)),
                        sellPrice = Price(BigDecimal("6000")),
                        createdAt = Clock.System.now().toEpochMilliseconds(),
                        unit = PosleUnit("Pcs"),
                        updatedAt = null
                    )
                )
            ),
            onClick = {},
            modifier = Modifier.padding(12.dp)
        )
    }
}