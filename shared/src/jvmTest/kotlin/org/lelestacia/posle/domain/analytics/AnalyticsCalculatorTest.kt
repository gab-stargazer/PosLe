package org.lelestacia.posle.domain.analytics

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.lelestacia.posle.util.toLocalDate
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyticsCalculatorTest {

    private val range = DateRange(
        startDate = AnalyticsTestData.dayOffset(-6),
        finishDate = AnalyticsTestData.dayOffset(1),
    )

    // ═══════════════════════════════════════════════════════════════════════
    //  Overview
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `calculate returns zeroed overview for empty input`() {
        val result = AnalyticsCalculator.calculate(transactions = emptyList(), range = range)

        assertEquals(BigDecimal.ZERO, result.overview.totalRevenue)
        assertEquals(BigDecimal.ZERO, result.overview.totalProfit)
        assertEquals(0, result.overview.totalTransactions)
        assertEquals(BigDecimal.ZERO, result.overview.totalItemsSold)
        assertTrue(result.timeSeries.isEmpty())
        assertTrue(result.topProducts.isEmpty())
    }

    @Test
    fun `calculate sums revenue profit and items sold across transactions`() {
        // T1: 2x Beras @ 90000, buy 80000 → revenue 180000, profit 20000
        val t1 = AnalyticsTestData.transaction(
            id = "1",
            createdAt = AnalyticsTestData.dayOffset(-1),
            items = listOf(
                AnalyticsTestData.productItem(
                    productId = "1",
                    name = "Beras 5kg",
                    quantity = "2",
                    sellPrice = "90000",
                    buyPrice = "80000",
                )
            ),
        )
        // T2: 3x Gula @ 18000, buy 15000 → revenue 54000, profit 9000
        val t2 = AnalyticsTestData.transaction(
            id = "2",
            createdAt = AnalyticsTestData.dayOffset(0),
            items = listOf(
                AnalyticsTestData.productItem(
                    productId = "2",
                    name = "Gula Pasir 1kg",
                    quantity = "3",
                    sellPrice = "18000",
                    buyPrice = "15000",
                )
            ),
        )

        val result = AnalyticsCalculator.calculate(transactions = listOf(t1, t2), range = range)

        assertEquals(BigDecimal("234000"), result.overview.totalRevenue)
        assertEquals(BigDecimal("29000"), result.overview.totalProfit)
        assertEquals(2, result.overview.totalTransactions)
        assertEquals(BigDecimal("5"), result.overview.totalItemsSold)
    }

    @Test
    fun `calculate multiplies bundle quantities for profit and top products`() {
        // 2x Paket Sembako, each contains Beras x1 (buy 80000, sell 90000) and Gula x2 (buy 15000, sell 18000)
        val bundle = AnalyticsTestData.transaction(
            id = "3",
            createdAt = AnalyticsTestData.dayOffset(-2),
            items = listOf(
                AnalyticsTestData.bundleItem(
                    bundleId = "10",
                    name = "Paket Sembako",
                    bundleQuantity = "2",
                    bundleSellPrice = "126000",
                    products = listOf(
                        AnalyticsTestData.bundleProduct(
                            productId = "1",
                            name = "Beras 5kg",
                            quantity = "1",
                            sellPrice = "90000",
                            buyPrice = "80000",
                        ),
                        AnalyticsTestData.bundleProduct(
                            productId = "2",
                            name = "Gula Pasir 1kg",
                            quantity = "2",
                            sellPrice = "18000",
                            buyPrice = "15000",
                        ),
                    ),
                )
            ),
        )

        val result = AnalyticsCalculator.calculate(transactions = listOf(bundle), range = range)

        // Revenue: 2 x 126000 (line-item basis)
        assertEquals(BigDecimal("252000"), result.overview.totalRevenue)
        // Profit: 2 x (1 x 10000 + 2 x 3000) = 2 x 16000 = 32000
        assertEquals(BigDecimal("32000"), result.overview.totalProfit)
        // Items sold: 2 bundles x (1 Beras + 2 Gula) = 6 product units
        assertEquals(BigDecimal("6"), result.overview.totalItemsSold)

        // Top product quantities: Beras 2 x 1 = 2, Gula 2 x 2 = 4
        val beras = result.topProducts.first { it.productId == "1" }
        assertEquals(BigDecimal("2"), beras.totalQuantity)
        // Top product revenue is line-item basis (item.quantity x sellPrice), same as overview:
        // Beras 2 x 90000 = 180000
        assertEquals(BigDecimal("180000"), beras.totalRevenue)
        val gula = result.topProducts.first { it.productId == "2" }
        assertEquals(BigDecimal("4"), gula.totalQuantity)
        // Gula 2 x 18000 = 36000 (NOT 2 x 2 x 18000)
        assertEquals(BigDecimal("36000"), gula.totalRevenue)
    }

    @Test
    fun `transactions outside range are excluded`() {
        val inside = AnalyticsTestData.transaction(
            id = "1",
            createdAt = AnalyticsTestData.dayOffset(0),
            items = listOf(
                AnalyticsTestData.productItem(
                    productId = "1",
                    name = "Beras 5kg",
                    quantity = "1",
                    sellPrice = "90000",
                    buyPrice = "80000",
                )
            ),
        )
        val outsideBefore = AnalyticsTestData.transaction(id = "2", createdAt = AnalyticsTestData.dayOffset(-10))
        val outsideAfter = AnalyticsTestData.transaction(id = "3", createdAt = AnalyticsTestData.dayOffset(5))

        val result = AnalyticsCalculator.calculate(
            transactions = listOf(inside, outsideBefore, outsideAfter),
            range = range,
        )

        assertEquals(1, result.overview.totalTransactions)
        assertEquals(BigDecimal("90000"), result.overview.totalRevenue)
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Time series
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `time series has one bucket per day in range`() {
        val result = AnalyticsCalculator.calculate(
            transactions = listOf(
                AnalyticsTestData.transaction(id = "1", createdAt = AnalyticsTestData.dayOffset(-1))
            ),
            range = range,
        )

        assertEquals(7, result.timeSeries.size)
        assertEquals(
            range.startDate.toLocalDate(),
            result.timeSeries.first().date,
        )
        assertEquals(
            range.finishDate.toLocalDate().minus(DatePeriod(days = 1)),
            result.timeSeries.last().date,
        )
    }

    @Test
    fun `time series buckets revenue by day`() {
        val result = AnalyticsCalculator.calculate(
            transactions = listOf(
                AnalyticsTestData.transaction(
                    id = "1",
                    createdAt = AnalyticsTestData.dayOffset(-3),
                    items = listOf(
                        AnalyticsTestData.productItem(
                            productId = "1",
                            name = "Beras 5kg",
                            quantity = "2",
                            sellPrice = "90000",
                            buyPrice = "80000",
                        )
                    ),
                )
            ),
            range = range,
        )

        val dayBucket = result.timeSeries.first { it.date == AnalyticsTestData.dayOffset(-3).toLocalDate() }
        assertEquals(BigDecimal("180000"), dayBucket.revenue)
        assertEquals(1, dayBucket.transactionCount)
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Top products
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `top products sorted by revenue descending`() {
        val low = AnalyticsTestData.transaction(
            id = "1",
            createdAt = AnalyticsTestData.dayOffset(-1),
            items = listOf(
                AnalyticsTestData.productItem(
                    productId = "1",
                    name = "Beras 5kg",
                    quantity = "1",
                    sellPrice = "90000",
                    buyPrice = "80000",
                )
            ),
        )
        val high = AnalyticsTestData.transaction(
            id = "2",
            createdAt = AnalyticsTestData.dayOffset(0),
            items = listOf(
                AnalyticsTestData.productItem(
                    productId = "2",
                    name = "Gula Pasir 1kg",
                    quantity = "10",
                    sellPrice = "18000",
                    buyPrice = "15000",
                )
            ),
        )

        val result = AnalyticsCalculator.calculate(transactions = listOf(low, high), range = range)

        assertEquals(2, result.topProducts.size)
        assertEquals("2", result.topProducts[0].productId) // Gula: 180000 > Beras: 90000
        assertEquals("1", result.topProducts[1].productId)
    }

    @Test
    fun `top products aggregate same product across transactions`() {
        val t1 = AnalyticsTestData.transaction(
            id = "1",
            createdAt = AnalyticsTestData.dayOffset(-2),
            items = listOf(
                AnalyticsTestData.productItem(
                    productId = "1",
                    name = "Beras 5kg",
                    quantity = "2",
                    sellPrice = "90000",
                    buyPrice = "80000",
                )
            ),
        )
        val t2 = AnalyticsTestData.transaction(
            id = "2",
            createdAt = AnalyticsTestData.dayOffset(-1),
            items = listOf(
                AnalyticsTestData.productItem(
                    productId = "1",
                    name = "Beras 5kg",
                    quantity = "3",
                    sellPrice = "90000",
                    buyPrice = "80000",
                )
            ),
        )

        val result = AnalyticsCalculator.calculate(transactions = listOf(t1, t2), range = range)

        assertEquals(1, result.topProducts.size)
        assertEquals(BigDecimal("5"), result.topProducts[0].totalQuantity)
        assertEquals(BigDecimal("450000"), result.topProducts[0].totalRevenue)
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Buckets & weeks
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `dailyBuckets is exclusive of finish date`() {
        val start = LocalDate(2023, 11, 13)
        val finish = LocalDate(2023, 11, 20)

        val buckets = AnalyticsCalculator.dailyBuckets(startDate = start, finishDate = finish)

        assertEquals(7, buckets.size)
        assertEquals(LocalDate(2023, 11, 13), buckets.first())
        assertEquals(LocalDate(2023, 11, 19), buckets.last())
    }

    @Test
    fun `aggregateToWeeks groups daily points into Monday-based weeks`() {
        val monday = LocalDate(2023, 11, 13)
        val points = listOf(
            TimeSeriesPoint(date = monday, revenue = BigDecimal("100")),
            TimeSeriesPoint(date = monday.plus(kotlinx.datetime.DatePeriod(days = 1)), revenue = BigDecimal("200")),
            TimeSeriesPoint(date = monday.plus(kotlinx.datetime.DatePeriod(days = 7)), revenue = BigDecimal("400")),
        )

        val weeks = AnalyticsCalculator.aggregateToWeeks(points)

        assertEquals(2, weeks.size)
        assertEquals(BigDecimal("300"), weeks[0].revenue)
        assertEquals(BigDecimal("400"), weeks[1].revenue)
        assertEquals(monday, weeks[0].date)
    }
}
