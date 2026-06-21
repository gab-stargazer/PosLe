package org.lelestacia.posle.screen.product_list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_add_product_to_category_shorts
import java.math.BigDecimal

@Composable
fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {

    ElevatedCard(
        shape = RoundedCornerShape(25F),
        modifier = modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {
            if (product.imageUri != null) {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .widthIn(max = 256.dp)
                        .aspectRatio(1F)
                )
            } else {
                Box(
                    modifier = Modifier
                        .widthIn(max = 256.dp)
                        .aspectRatio(1F)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            Text(
                text = product.name.value,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "${product.price.value.toRupiah()}/${product.unit.value}",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
fun ProductAddItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    ElevatedCard(
        shape = RoundedCornerShape(25F),
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .widthIn(max = 256.dp)
                .aspectRatio(0.7F)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = Icons.Default.Add.name,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    stringResource(Res.string.title_add_product_to_category_shorts),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewProductItem() {
    AppTheme {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            ProductItem(
                product = Product(
                    id = 0,
                    name = Name("Salak"),
                    unit = org.lelestacia.posle.util.Unit(value = "Kg"),
                    price = Price(BigDecimal("100000"))
                ),
                onClick = {},
                onLongClick = {},
                modifier = Modifier.padding(12.dp)
            )

            ProductAddItem(
                onClick = {},
                modifier = Modifier
                    .widthIn(128.dp)
                    .padding(12.dp)
            )
        }
    }
}