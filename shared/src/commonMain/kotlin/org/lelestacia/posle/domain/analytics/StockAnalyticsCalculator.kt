package org.lelestacia.posle.domain.analytics

import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.model.StockMovement
import java.math.BigDecimal

/**
 * Pure aggregation logic for stock movement analytics (fast/slow movers).
 *
 * Ranking is based on the total absolute quantity sold ([StockMovementType.Sale])
 * within the selected date range. All other movement types (purchases, returns,
 * adjustments) do not contribute to the mover ranking.
 */
object StockAnalyticsCalculator {

    /**
     * Ranks products by their total sold quantity into fast movers (top N, descending)
     * and slow movers (bottom N, ascending, only products with at least one sale).
     *
     * @param movements Stock movements for the selected range (newest first is fine).
     * @param topN Maximum number of fast movers to return.
     * @param bottomN Maximum number of slow movers to return.
     */
    fun rankMovers(
        movements: List<StockMovement>,
        topN: Int = 5,
        bottomN: Int = 5,
    ): StockAnalyticsResult {
        if (movements.isEmpty()) return StockAnalyticsResult()

        val soldByProduct = mutableMapOf<Int, MutableList<StockMovement>>()
        movements.forEach { movement ->
            if (movement.movementType == StockMovementType.Sale) {
                soldByProduct.getOrPut(movement.productId) { mutableListOf() }.add(movement)
            }
        }

        val ranked = soldByProduct.entries
            .map { (productId, productMovements) ->
                val first = productMovements.first()
                StockProductAnalytics(
                    productId = productId,
                    productName = first.productName,
                    productUnit = first.productUnit,
                    soldQuantity = productMovements.fold(BigDecimal.ZERO) { acc, movement ->
                        acc + movement.amount.value.abs()
                    },
                )
            }
            .sortedWith(
                compareByDescending<StockProductAnalytics> { it.soldQuantity }
                    .thenBy { it.productName.value }
            )

        val fastMovers = ranked.take(topN)
        val slowMovers = ranked.asReversed().take(bottomN)

        return StockAnalyticsResult(
            fastMovers = fastMovers,
            slowMovers = slowMovers,
        )
    }
}
