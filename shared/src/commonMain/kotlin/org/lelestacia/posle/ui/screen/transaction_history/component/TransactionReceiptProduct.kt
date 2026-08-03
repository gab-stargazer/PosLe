package org.lelestacia.posle.ui.screen.transaction_history.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.TransactionProduct
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_subtotal
import posle.shared.generated.resources.label_note_format

@Composable
fun TransactionReceiptProduct(
    product: TransactionProduct,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            Text(
                text = product.productName.value.uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                )
            )

            Text(
                text = product.sellPrice.value.toRupiah().uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "\t${product.quantity.value.toDisplayText()} ${product.unit.value}".uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.weight(1F)
            )
        }

        val subtotal = product
            .quantity
            .value * product.sellPrice.value

        Text(
            stringResource(
                Res.string.title_subtotal,
                subtotal.toRupiah().uppercase()
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (product.note.orEmpty().isNotBlank()) {
            Text(
                stringResource(Res.string.label_note_format, product.note.orEmpty()),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductCartItem() {
    AppTheme {
        Box(
            modifier = Modifier.padding(12.dp)
        ) {
            TransactionReceiptProduct(
                product = TransactionProduct(
                    productId = 0,
                    productName = Name("Nasi"),
                    skuNumber = null,
                    imageUri = null,
                    buyPrice = Price(10000.toBigDecimal()),
                    sellPrice = Price(12000.toBigDecimal()),
                    unit = Unit("porsi"),
                    quantity = Amount(java.math.BigDecimal("3")),
                    note = null,
                    variants = emptyList()
                )
            )
        }
    }
}