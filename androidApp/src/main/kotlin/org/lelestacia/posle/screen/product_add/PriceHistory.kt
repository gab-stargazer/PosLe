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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.ProductPriceHistory
import org.lelestacia.posle.util.toFormattedDate
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.subtitle_date
import posle.shared.generated.resources.subtitle_price

@Composable
fun PriceHistory(
    priceHistoryPaging: List<ProductPriceHistory>,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 12.dp)
                .padding(vertical = 6.dp)
        ) {
            Text(
                stringResource(Res.string.subtitle_date),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.weight(1F)
            )
            Text(
                stringResource(Res.string.subtitle_price),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.weight(1F)
            )
        }

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
}