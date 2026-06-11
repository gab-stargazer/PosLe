package org.lelestacia.posle.screen.product_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.App
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.item_product_volatile
import java.math.BigDecimal

@Composable
fun ProductItemUI(
    product: Product,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(25F),
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = when (product.imageUri != null) {
                        true -> 0.dp
                        false -> 12.dp
                    }, end = 12.dp
                )
                .height(IntrinsicSize.Min)
        ) {

            if (product.imageUri != null) {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(128.dp)
                        .aspectRatio(1F)
                        .padding(end = 12.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(product.name.value, style = MaterialTheme.typography.labelMedium)
                    IconButton(
                        onClick = onEdit
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                }

                Text(
                    "${product.price.value.toRupiah()}/${product.unit.value}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 6.dp)
                )
                if (product.isProductVolatile) {
                    Text(
                        stringResource(Res.string.item_product_volatile),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.End
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductItemUI() {
    App {
        ProductItemUI(
            product = Product(
                id = 0,
                name = Name("Salak"),
                unit = org.lelestacia.posle.util.Unit("Kg"),
                price = Price(BigDecimal("100000")),
                isProductVolatile = true
            ),
            onEdit = {

            },
            modifier = Modifier.padding(12.dp)
        )
    }
}