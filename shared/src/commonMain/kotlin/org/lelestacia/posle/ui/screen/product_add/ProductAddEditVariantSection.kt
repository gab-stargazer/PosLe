package org.lelestacia.posle.ui.screen.product_add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_additional_price
import posle.shared.generated.resources.label_variation_and_addition
import java.math.BigDecimal

@Composable
fun ProductAddEditVariantSection(
    variants: List<Variant>,
    onAddVariantClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 6.dp)
        ) {
            Text(
                stringResource(Res.string.label_variation_and_addition),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 6.dp)
            )

            IconButton(
                onClick = onAddVariantClicked
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            variants.forEach { variant ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SubdirectoryArrowRight,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1F)
                            .padding(start = 3.dp)
                    ) {
                        Text(
                            variant.name.value,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        val priceSb = buildAnnotatedString {
                            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                                append(stringResource(Res.string.label_additional_price))
                            }

                            withStyle(
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ).toSpanStyle()
                            ) {
                                append(variant.priceAdjustment.value.toRupiah())
                            }
                        }

                        Text(priceSb)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductAddEditVariantSection() {
    AppTheme {
        ProductAddEditVariantSection(
            variants = listOf(
                Variant(
                    id = 1,
                    name = Name("Karung"),
                    priceAdjustment = Price(BigDecimal.ZERO)
                ),
                Variant(
                    id = 2,
                    name = Name("Pedas"),
                    priceAdjustment = Price(BigDecimal(2000))
                ),
                Variant(
                    id = 3,
                    name = Name("Ekstra Nasi"),
                    priceAdjustment = Price(BigDecimal(5000))
                ),
                Variant(
                    id = 4,
                    name = Name("Telur Ceplok"),
                    priceAdjustment = Price(BigDecimal(3000))
                ),
                Variant(
                    id = 5,
                    name = Name("Tanpa Bawang"),
                    priceAdjustment = Price(BigDecimal.ZERO)
                )
            ),
            onAddVariantClicked = {}
        )
    }
}
