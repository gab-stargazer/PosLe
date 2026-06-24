package org.lelestacia.posle.screen.product_add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.domain.model.ProductPriceHistory
import org.lelestacia.posle.util.toFormattedDate
import org.lelestacia.posle.util.toRupiah

@Composable
fun PriceHistory(
    priceHistoryPaging: List<ProductPriceHistory>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        priceHistoryPaging.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Text(
                    it.createdAt.toFormattedDate(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 12.dp)
                )
                Text(
                    it.price.value.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1F)
                )
            }
        }
    }
}