package org.lelestacia.posle.domain.analytics

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.TransactionProduct
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.toLocalDate
import java.math.BigDecimal

/**
 * Pure aggregation logic for sales analytics.
 *
 * All functions are deterministic and free of I/O so they can be unit-tested
 * without any database or platform dependency.
 */
object AnalyticsCalculator {

    /**
     * Calculates the sales overview, daily time series and top products
     * for the given [transactions] restricted to [range].
     *
     * Aggregation rules:
     * - Revenue per transaction = Σ over items of `quantity × sellPrice` (line-item basis, matches receipts).
     * - Profit per transaction = Σ over all items' products of
     *   `unitsSold(item, product) × (product.sellPrice − product.buyPrice)`,
     *   where `unitsSold` is `product.quantity` for Product items and
     *   `item.quantity × product.quantity` for Bundle items — the same basis
     *   the transaction recap and stock movements use, so profit stays consistent
     *   with both.
     * - Total items sold = Σ over all items' products of `unitsSold(item, product)`.
     *   A bundle therefore counts every product unit inside it, not one line item.
     * - Time series bucketed by `transaction.createdAt.toLocalDate()` in the range's time zone.
     * - Top products keyed by `productId`, aggregated across Product and Bundle item types.
     *   Quantity uses the same stock-movement basis as [unitsSold]; revenue uses
     *   the line-item basis (item.quantity × sellPrice) so it sums consistently with the overview.
     */
    fun calculate(transactions: List<Transaction>, range: DateRange): AnalyticsResult {
        if (transactions.isEmpty()) return AnalyticsResult()

        val inRangeTransactions = transactions.filter { transaction ->
            transaction.createdAt >= range.startDate && transaction.createdAt < range.finishDate
        }

        var totalRevenue = BigDecimal.ZERO
        var totalProfit = BigDecimal.ZERO
        var totalItemsSold = BigDecimal.ZERO

        val revenueByDay = mutableMapOf<LocalDate, BigDecimal>()
        val profitByDay = mutableMapOf<LocalDate, BigDecimal>()
        val transactionCountByDay = mutableMapOf<LocalDate, Int>()

        val productAccumulators = mutableMapOf<String, TopProductAccumulator>()

        inRangeTransactions.forEach { transaction ->
            val transactionRevenue = transaction.items.fold(BigDecimal.ZERO) { acc, item ->
                acc + item.quantity.value * item.sellPrice.value
            }
            val transactionProfit = transaction.items.fold(BigDecimal.ZERO) { acc, item ->
                item.products.fold(acc) { itemAcc, product ->
                    itemAcc + unitsSold(item, product) * (product.sellPrice.value - product.buyPrice.value)
                }
            }
            val transactionItemsSold = transaction.items.fold(BigDecimal.ZERO) { acc, item ->
                item.products.fold(acc) { itemAcc, product ->
                    itemAcc + unitsSold(item, product)
                }
            }

            totalRevenue += transactionRevenue
            totalProfit += transactionProfit
            totalItemsSold += transactionItemsSold

            val day = transaction.createdAt.toLocalDate()
            revenueByDay[day] = (revenueByDay[day] ?: BigDecimal.ZERO) + transactionRevenue
            profitByDay[day] = (profitByDay[day] ?: BigDecimal.ZERO) + transactionProfit
            transactionCountByDay[day] = (transactionCountByDay[day] ?: 0) + 1

            transaction.items.forEach { item ->
                item.products.forEach { product ->
                    val key = product.productId
                    val accumulator = productAccumulators.getOrPut(key) {
                        TopProductAccumulator(productName = product.productName)
                    }
                    // Quantity on the same basis as stock movements (matches fast/slow movers).
                    val quantity = unitsSold(item, product)
                    accumulator.addQuantity(quantity)
                    // Revenue on the same line-item basis as the overview total (matches receipts).
                    accumulator.addRevenue(item.quantity.value * product.sellPrice.value)
                }
            }
        }

        val startDate = range.startDate.toLocalDate()
        val finishDate = range.finishDate.toLocalDate()
        val buckets = dailyBuckets(startDate = startDate, finishDate = finishDate)
        val timeSeries = buckets.map { day ->
            TimeSeriesPoint(
                date = day,
                revenue = revenueByDay[day] ?: BigDecimal.ZERO,
                profit = profitByDay[day] ?: BigDecimal.ZERO,
                transactionCount = transactionCountByDay[day] ?: 0,
            )
        }

        val topProducts = productAccumulators.entries
            .sortedWith(
                compareByDescending<Map.Entry<String, TopProductAccumulator>> { it.value.totalRevenue }
                    .thenBy { it.value.productName.value }
            )
            .map { (productId, accumulator) ->
                TopProduct(
                    productId = productId,
                    productName = accumulator.productName,
                    totalQuantity = accumulator.totalQuantity,
                    totalRevenue = accumulator.totalRevenue,
                )
            }

        return AnalyticsResult(
            overview = AnalyticsOverview(
                totalRevenue = totalRevenue,
                totalProfit = totalProfit,
                totalTransactions = inRangeTransactions.size,
                totalItemsSold = totalItemsSold,
            ),
            timeSeries = timeSeries,
            topProducts = topProducts,
        )
    }

    /**
     * Returns the list of days between [startDate] and [finishDate] (exclusive end).
     */
    fun dailyBuckets(startDate: LocalDate, finishDate: LocalDate): List<LocalDate> {
        val result = mutableListOf<LocalDate>()
        var current = startDate
        while (current < finishDate) {
            result.add(current)
            current = current.plus(DatePeriod(days = 1))
        }
        return result
    }

    /**
     * Aggregates a daily [timeSeries] into ISO-week buckets (Monday-based).
     * Points are grouped by the week containing their date; the bucket date is the week's Monday.
     */
    fun aggregateToWeeks(timeSeries: List<TimeSeriesPoint>): List<TimeSeriesPoint> {
        if (timeSeries.isEmpty()) return emptyList()

        val grouped = linkedMapOf<LocalDate, MutableList<TimeSeriesPoint>>()
        timeSeries.forEach { point ->
            val weekStart = point.date.toWeekStart()
            grouped.getOrPut(weekStart) { mutableListOf() }.add(point)
        }

        return grouped.map { (weekStart, points) ->
            TimeSeriesPoint(
                date = weekStart,
                label = points.first().date.weekLabel(),
                revenue = points.fold(BigDecimal.ZERO) { acc, point -> acc + point.revenue },
                profit = points.fold(BigDecimal.ZERO) { acc, point -> acc + point.profit },
                transactionCount = points.fold(0) { acc, point -> acc + point.transactionCount },
            )
        }
    }

    /**
     * Returns the Monday of the week containing this date (ISO-8601, Monday-based).
     */
    fun LocalDate.toWeekStart(): LocalDate {
        // DayOfWeek.MONDAY.ordinal == 0 ... SUNDAY.ordinal == 6
        val daysSinceMonday = dayOfWeek.ordinal
        return minus(DatePeriod(days = daysSinceMonday))
    }

    /**
     * Week label in the form `dd MMM` of the week's first day (used for chart axes).
     */
    private fun LocalDate.weekLabel(): String {
        return "${day} ${month.name.lowercase().take(3)}"
    }

    /**
     * Number of product units sold for one [item]'s [product] line.
     *
     * Product purchases store the quantity redundantly (the item-level quantity
     * equals the product-level quantity, see `TransactionRepositoryImpl`), so the
     * product-level quantity alone is the unit count. Bundle purchases store the
     * bundle count at item level and the per-bundle amount at product level, so
     * the two multiply. This is exactly the basis the transaction recap and stock
     * movements use.
     */
    private fun unitsSold(item: TransactionItem, product: TransactionProduct): BigDecimal =
        when (item.type) {
            TransactionItemType.Product -> product.quantity.value
            TransactionItemType.Bundle -> item.quantity.value * product.quantity.value
        }

    private data class TopProductAccumulator(
        val productName: Name,
        var totalQuantity: BigDecimal = BigDecimal.ZERO,
        var totalRevenue: BigDecimal = BigDecimal.ZERO,
    ) {
        fun addQuantity(quantity: BigDecimal) {
            totalQuantity += quantity
        }

        fun addRevenue(revenue: BigDecimal) {
            totalRevenue += revenue
        }
    }
}
