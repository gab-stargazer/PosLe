package org.lelestacia.posle.domain.analytics

import org.lelestacia.posle.data.entity.StockMovementType
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StockAnalyticsCalculatorTest {

    @Test
    fun `fast movers ranked by total sold quantity descending`() {
        val movements = listOf(
            AnalyticsTestData.stockMovement(productId = "1", productName = "Beras 5kg", amount = "-10"),
            AnalyticsTestData.stockMovement(productId = "2", productName = "Gula Pasir 1kg", amount = "-3"),
            AnalyticsTestData.stockMovement(productId = "3", productName = "Kopi Bubuk", amount = "-7"),
        )

        val result = StockAnalyticsCalculator.rankMovers(movements)

        assertEquals(listOf("1", "3", "2"), result.fastMovers.map { it.productId })
        assertEquals(BigDecimal("10"), result.fastMovers[0].soldQuantity)
        assertEquals(BigDecimal("7"), result.fastMovers[1].soldQuantity)
        assertEquals(BigDecimal("3"), result.fastMovers[2].soldQuantity)
    }

    @Test
    fun `only sale movements count towards ranking`() {
        val movements = listOf(
            AnalyticsTestData.stockMovement(productId = "1", productName = "Beras 5kg", amount = "-10"),
            AnalyticsTestData.stockMovement(productId = "2", productName = "Gula Pasir 1kg", amount = "-5"),
            AnalyticsTestData.stockMovement(
                productId = "3",
                productName = "Kopi Bubuk",
                amount = "50",
                movementType = StockMovementType.Purchase,
            ),
            AnalyticsTestData.stockMovement(
                productId = "4",
                productName = "Teh Celup",
                amount = "-20",
                movementType = StockMovementType.AdjustmentDecrease,
            ),
        )

        val result = StockAnalyticsCalculator.rankMovers(movements)

        assertEquals(listOf("1", "2"), result.fastMovers.map { it.productId })
        assertTrue(result.slowMovers.map { it.productId }.toSet() == setOf("1", "2"))
    }

    @Test
    fun `sale amounts are summed with abs so negative values rank correctly`() {
        val movements = listOf(
            AnalyticsTestData.stockMovement(productId = "1", productName = "Beras 5kg", amount = "-2"),
            AnalyticsTestData.stockMovement(productId = "1", productName = "Beras 5kg", amount = "-3"),
            AnalyticsTestData.stockMovement(productId = "2", productName = "Gula Pasir 1kg", amount = "-4"),
        )

        val result = StockAnalyticsCalculator.rankMovers(movements)

        val beras = result.fastMovers.first { it.productId == "1" }
        assertEquals(BigDecimal("5"), beras.soldQuantity)
        assertEquals("1", result.fastMovers[0].productId) // 5 > 4
    }

    @Test
    fun `slow movers are bottom N ascending`() {
        val movements = listOf(
            AnalyticsTestData.stockMovement(productId = "1", productName = "Beras 5kg", amount = "-10"),
            AnalyticsTestData.stockMovement(productId = "2", productName = "Gula Pasir 1kg", amount = "-3"),
            AnalyticsTestData.stockMovement(productId = "3", productName = "Kopi Bubuk", amount = "-7"),
        )

        val result = StockAnalyticsCalculator.rankMovers(movements, topN = 2, bottomN = 2)

        assertEquals(listOf("2", "3"), result.slowMovers.map { it.productId })
    }

    @Test
    fun `ties broken deterministically by product name`() {
        val movements = listOf(
            AnalyticsTestData.stockMovement(productId = "1", productName = "Beras 5kg", amount = "-5"),
            AnalyticsTestData.stockMovement(productId = "2", productName = "Gula Pasir 1kg", amount = "-5"),
        )

        val result = StockAnalyticsCalculator.rankMovers(movements)

        assertEquals(listOf("1", "2"), result.fastMovers.map { it.productId })
    }

    @Test
    fun `empty movements return empty result`() {
        val result = StockAnalyticsCalculator.rankMovers(emptyList())

        assertTrue(result.fastMovers.isEmpty())
        assertTrue(result.slowMovers.isEmpty())
    }

    @Test
    fun `product name and unit come from movement snapshot`() {
        val movements = listOf(
            AnalyticsTestData.stockMovement(productId = "7", productName = "Air Mineral 600ml", amount = "-12"),
        )

        val result = StockAnalyticsCalculator.rankMovers(movements)

        assertEquals("Air Mineral 600ml", result.fastMovers[0].productName.value)
        assertEquals("pcs", result.fastMovers[0].productUnit.value)
    }
}
