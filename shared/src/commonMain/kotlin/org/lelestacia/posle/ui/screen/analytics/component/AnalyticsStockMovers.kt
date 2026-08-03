package org.lelestacia.posle.ui.screen.analytics.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.analytics.StockProductAnalytics
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.util.toDisplayText
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_fast_movers
import posle.shared.generated.resources.label_needs_restock
import posle.shared.generated.resources.label_no_data
import posle.shared.generated.resources.label_slow_movers
import posle.shared.generated.resources.label_sold
import posle.shared.generated.resources.label_stock_format

/**
 * Renders one of the stock mover lists (fast movers or slow movers).
 *
 * @param title Resolved string resource title (fast/slow movers).
 * @param movers The ranked list of products.
 */
@Composable
fun AnalyticsStockMovers(
    title: String,
    movers: List<StockProductAnalytics>,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (movers.isEmpty()) {
                Text(
                    text = stringResource(Res.string.label_no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                movers.forEachIndexed { index, mover ->
                    Column {
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1F)) {
                                Text(
                                    text = mover.productName.value,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = stringResource(
                                        Res.string.label_sold,
                                        "${mover.soldQuantity.toDisplayText()} ${mover.productUnit.value}"
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stringResource(
                                        Res.string.label_stock_format,
                                        mover.currentStock.toDisplayText(),
                                        mover.productUnit.value
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (mover.needsRestock) {
                                Text(
                                    text = stringResource(Res.string.label_needs_restock),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier
                                        .background(
                                            color = BurgundyRed,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Convenience wrappers that resolve the Indonesian titles.
 */
@Composable
fun AnalyticsFastMovers(
    movers: List<StockProductAnalytics>,
    modifier: Modifier = Modifier,
) {
    AnalyticsStockMovers(
        title = stringResource(Res.string.label_fast_movers),
        movers = movers,
        modifier = modifier
    )
}

@Composable
fun AnalyticsSlowMovers(
    movers: List<StockProductAnalytics>,
    modifier: Modifier = Modifier,
) {
    AnalyticsStockMovers(
        title = stringResource(Res.string.label_slow_movers),
        movers = movers,
        modifier = modifier
    )
}
