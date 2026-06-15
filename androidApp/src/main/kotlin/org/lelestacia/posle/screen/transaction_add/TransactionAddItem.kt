package org.lelestacia.posle.screen.transaction_add

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.toRupiah

@Composable
fun TransactionAddItem(
    product: Product,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(
                topStart = 25F,
                topEnd = 25F,
                bottomStart = 25F
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .animateContentSize()
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
                            .width(96.dp)
                            .aspectRatio(1F)
                            .padding(all = 6.dp)
                            .clip(RoundedCornerShape(25F))
                    )
                }

                Column(
                    horizontalAlignment =
                        when (product.imageUri != null) {
                            true -> Alignment.End
                            false -> Alignment.Start
                        },
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 12.dp)
                        .padding(end = 12.dp)
                ) {
                    Text(product.name.value, style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${product.price.value.toRupiah()}/${product.unit.value}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                IconButton(
                    onClick = onAdd
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddItem() {
    AppTheme {
        val products = org.lelestacia.posle.util.SampleData.products
        val sateAyam = products[0]

        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TransactionAddItem(
                product = sateAyam,
                onAdd = {},
            )
        }
    }
}
