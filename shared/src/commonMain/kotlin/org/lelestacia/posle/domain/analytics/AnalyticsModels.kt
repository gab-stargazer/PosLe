package org.lelestacia.posle.domain.analytics

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.lelestacia.posle.util.BigDecimalSerializer
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.getTodayRangeMilliseconds
import java.math.BigDecimal

/**
 * A date range bounded by [startDate] (inclusive, epoch ms) and [finishDate] (exclusive, epoch ms).
 */
@Serializable
data class DateRange(
    val startDate: Long,
    val finishDate: Long,
) {
    companion object {
        fun today(): DateRange {
            val (start, finish) = getTodayRangeMilliseconds()
            return DateRange(startDate = start, finishDate = finish)
        }

        fun last7Days(): DateRange {
            val (todayStart, startOfTomorrow) = getTodayRangeMilliseconds()
            val sevenDaysMs = 7L * 24 * 60 * 60 * 1000
            return DateRange(
                startDate = todayStart - (6L * 24 * 60 * 60 * 1000),
                finishDate = startOfTomorrow
            )
        }

        fun last30Days(): DateRange {
            val (todayStart, startOfTomorrow) = getTodayRangeMilliseconds()
            return DateRange(
                startDate = todayStart - (29L * 24 * 60 * 60 * 1000),
                finishDate = startOfTomorrow
            )
        }
    }
}

@Serializable
data class AnalyticsOverview(
    @Serializable(with = BigDecimalSerializer::class)
    val totalRevenue: BigDecimal = BigDecimal.ZERO,
    @Serializable(with = BigDecimalSerializer::class)
    val totalProfit: BigDecimal = BigDecimal.ZERO,
    val totalTransactions: Int = 0,
    @Serializable(with = BigDecimalSerializer::class)
    val totalItemsSold: BigDecimal = BigDecimal.ZERO,
)

@Serializable
data class TimeSeriesPoint(
    val date: LocalDate,
    val label: String = "",
    @Serializable(with = BigDecimalSerializer::class)
    val revenue: BigDecimal = BigDecimal.ZERO,
    @Serializable(with = BigDecimalSerializer::class)
    val profit: BigDecimal = BigDecimal.ZERO,
    val transactionCount: Int = 0,
)

@Serializable
data class TopProduct(
    val productId: Int,
    val productName: Name,
    @Serializable(with = BigDecimalSerializer::class)
    val totalQuantity: BigDecimal = BigDecimal.ZERO,
    @Serializable(with = BigDecimalSerializer::class)
    val totalRevenue: BigDecimal = BigDecimal.ZERO,
)

@Serializable
data class AnalyticsResult(
    val overview: AnalyticsOverview = AnalyticsOverview(),
    val timeSeries: List<TimeSeriesPoint> = emptyList(),
    val topProducts: List<TopProduct> = emptyList(),
)

@Serializable
data class StockProductAnalytics(
    val productId: Int,
    val productName: Name,
    val productUnit: Unit,
    @Serializable(with = BigDecimalSerializer::class)
    val soldQuantity: BigDecimal = BigDecimal.ZERO,
    @Serializable(with = BigDecimalSerializer::class)
    val currentStock: BigDecimal = BigDecimal.ZERO,
    val needsRestock: Boolean = false,
)

@Serializable
data class StockAnalyticsResult(
    val fastMovers: List<StockProductAnalytics> = emptyList(),
    val slowMovers: List<StockProductAnalytics> = emptyList(),
)
