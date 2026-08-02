package org.lelestacia.posle.ui.screen.bundle_add_edit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleProductState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.txt_individual_price
import posle.shared.generated.resources.txt_modal_price

@Composable
fun BundleItem(
    bundledProduct: BundleProductState,
    onQuantityChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = bundledProduct.product.name.value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                val modalPriceSb = buildAnnotatedString {
                    withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                        append(stringResource(Res.string.txt_modal_price))
                    }

                    withStyle(
                        MaterialTheme.typography.bodyMedium
                            .copy(fontWeight = FontWeight.Bold)
                            .toSpanStyle()
                    ) {
                        append(bundledProduct.product.buyPrice.value.toRupiah())
                    }
                }

                Text(
                    text = modalPriceSb,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                val individualPriceSb = buildAnnotatedString {
                    withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                        append(stringResource(Res.string.txt_individual_price))
                    }

                    withStyle(
                        MaterialTheme.typography.bodyMedium
                            .copy(fontWeight = FontWeight.Bold)
                            .toSpanStyle()
                    ) {
                        append(bundledProduct.product.sellPrice.value.toRupiah())
                    }
                }

                Text(
                    text = individualPriceSb,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = Icons.Default.Delete.name,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        BundlePriceAndQty(
            productUnit = bundledProduct.product.unit,
            quantity = bundledProduct.quantity,
            quantityError = bundledProduct.quantityError,
            onQuantityChange = onQuantityChange,
            price = bundledProduct.sellPrice,
            priceError = bundledProduct.sellPriceError,
            onPriceChange = onPriceChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, end = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBundleItem() {
    AppTheme {
        BundleItem(
            bundledProduct = BundleProductState(
                product = SampleData.products.first(),
            ),
            onQuantityChange = {},
            onPriceChange = {},
            onDelete = {},
            modifier = Modifier.padding(12.dp)
        )
    }
}