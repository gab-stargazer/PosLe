package org.lelestacia.posle.ui.screen.product_list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
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
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.toRupiah

@TraceRecomposition
@Composable
fun BundleItem(
    modifier: Modifier = Modifier,
    bundle: Bundle,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val totalPrice = bundle.bundleProducts.sumOf {
        it.sellPrice.value * it.quantity.value
    }

    ElevatedCard(
        shape = RoundedCornerShape(25F),
        modifier = modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .height(IntrinsicSize.Min)
    ) {
        Row {
            if (bundle.imageUri != null) {
                AsyncImage(
                    model = bundle.imageUri,
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
                    text = bundle.name.value,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Column {
                    Text(
                        text = "Harga Paket: ${totalPrice.toRupiah()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                    )
                    
                    Text(
                        text = "${bundle.bundleProducts.size} Produk dalam Paket",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBundleItem() {
    AppTheme {
        BundleItem(
            bundle = Bundle(
                id = 0,
                name = Name("Paket Sembako"),
                bundleProducts = emptyList(),
                createdAt = 0
            ),
            onClick = {},
            modifier = Modifier.padding(12.dp)
        )
    }
}
