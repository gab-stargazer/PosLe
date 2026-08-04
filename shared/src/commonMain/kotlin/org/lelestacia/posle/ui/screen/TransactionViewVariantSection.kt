package org.lelestacia.posle.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_variant
import java.math.BigDecimal

@Composable
fun TransactionViewVariantSection(
    variants: List<Variant>,
    amount: Any,
    totalPrice: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            stringResource(Res.string.label_variant),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 12.dp)
        )

        variants.forEach { variant ->
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.SubdirectoryArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = "${variant.name.value} x $amount",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = variant.priceAdjustment.value.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = totalPrice,
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNasiGorengVariants() {
    AppTheme {
        TransactionViewVariantSection(
            variants = listOf(
                Variant(
                    id = "1",
                    name = Name("Telur Ceplok"),
                    priceAdjustment = Price(BigDecimal("3000"))
                ),
                Variant(
                    id = "2",
                    name = Name("Kerupuk Udang"),
                    priceAdjustment = Price(BigDecimal("2000"))
                ),
                Variant(
                    id = "3",
                    name = Name("Ayam Suwir"),
                    priceAdjustment = Price(BigDecimal("5000"))
                )
            ),
            amount = 1,
            totalPrice = BigDecimal("25000").toRupiah(),
            modifier = Modifier.padding(16.dp)
        )
    }
}
