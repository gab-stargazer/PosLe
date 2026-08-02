package org.lelestacia.posle.ui.screen.product_list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.skydoves.compose.stability.runtime.TraceRecomposition
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_add_product_to_category_shorts
import java.math.BigDecimal

@TraceRecomposition
@Composable
fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    isStockTracked: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {

    ElevatedCard(
        shape = RoundedCornerShape(25F),
        modifier = modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .height(IntrinsicSize.Min)
    ) {
        Row {
            if (product.imageUri != null) {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .aspectRatio(1F)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .aspectRatio(1F)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(all = 12.dp)
            ) {


                Text(
                    text = product.name.value,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Column {
                    Text(
                        text = "Harga: ${product.sellPrice.value.toRupiah()}",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                    )

                    val stock = product.stock.value.toDisplayText()

                    if (isStockTracked) {
                        Text(
                            text = "Stok: $stock ${product.unit.value}",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
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
                .width(128.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .height(Util.GridItemHeight + Util.GridItemSpacing)
        ) {
            ProductItem(
                product = Product(
                    id = 0,
                    name = Name("Salak"),
                    stock = Amount(BigDecimal("100")),
                    unit = org.lelestacia.posle.util.Unit(value = "Kg"),
                    buyPrice = Price(BigDecimal("15000")),
                    sellPrice = Price(BigDecimal("15000")),
                ),
                isStockTracked = true,
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