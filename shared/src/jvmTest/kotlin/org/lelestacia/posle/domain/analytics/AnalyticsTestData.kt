package org.lelestacia.posle.domain.analytics

import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.TransactionProduct
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit
import java.math.BigDecimal

/**
 * Test fixtures for analytics calculator tests.
 *
 * Timestamps are built from fixed dates in the JVM's default zone so that
 * `toLocalDate()` (which uses `TimeZone.currentSystemDefault()`) maps them back
 * to the exact same calendar days on any machine.
 */
object AnalyticsTestData {

    /** 2023-11-14, a Tuesday. */
    val baseDate: java.time.LocalDate = java.time.LocalDate.of(2023, 11, 14)

    private val zone: java.time.ZoneId = java.time.ZoneId.systemDefault()

    /** Epoch ms for `baseDate.plusDays(days)` at 12:00 local (midday avoids DST/day-boundary issues). */
    fun dayOffset(days: Long): Long =
        baseDate.plusDays(days).atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

    fun transaction(
        id: String,
        createdAt: Long,
        customerName: String = "Pelanggan $id",
        items: List<TransactionItem> = emptyList(),
    ): Transaction {
        return Transaction(
            id = id,
            customerName = Name(customerName),
            items = items,
            isRecapped = false,
            createdAt = createdAt,
            updatedAt = null,
        )
    }

    fun productItem(
        productId: String,
        name: String,
        quantity: String,
        sellPrice: String,
        buyPrice: String,
    ): TransactionItem {
        return TransactionItem(
            id = productId,
            type = TransactionItemType.Product,
            referenceId = productId,
            name = Name(name),
            quantity = Amount(BigDecimal(quantity)),
            sellPrice = Price(BigDecimal(sellPrice)),
            note = null,
            products = listOf(
                TransactionProduct(
                    productId = productId,
                    productName = Name(name),
                    skuNumber = null,
                    imageUri = null,
                    buyPrice = Price(BigDecimal(buyPrice)),
                    sellPrice = Price(BigDecimal(sellPrice)),
                    unit = Unit("pcs"),
                    note = null,
                    quantity = Amount(BigDecimal(quantity)),
                    variants = emptyList<Variant>(),
                )
            ),
            createdAt = 0L,
            updatedAt = null,
        )
    }

    fun bundleItem(
        bundleId: String,
        name: String,
        bundleQuantity: String,
        bundleSellPrice: String,
        products: List<TransactionProduct>,
    ): TransactionItem {
        return TransactionItem(
            id = bundleId,
            type = TransactionItemType.Bundle,
            referenceId = bundleId,
            name = Name(name),
            quantity = Amount(BigDecimal(bundleQuantity)),
            sellPrice = Price(BigDecimal(bundleSellPrice)),
            note = null,
            products = products,
            createdAt = 0L,
            updatedAt = null,
        )
    }

    fun bundleProduct(
        productId: String,
        name: String,
        quantity: String,
        sellPrice: String,
        buyPrice: String,
    ): TransactionProduct {
        return TransactionProduct(
            productId = productId,
            productName = Name(name),
            skuNumber = null,
            imageUri = null,
            buyPrice = Price(BigDecimal(buyPrice)),
            sellPrice = Price(BigDecimal(sellPrice)),
            unit = Unit("pcs"),
            note = null,
            quantity = Amount(BigDecimal(quantity)),
            variants = emptyList<Variant>(),
        )
    }

    fun stockMovement(
        productId: String,
        productName: String,
        amount: String,
        movementType: StockMovementType = StockMovementType.Sale,
        createdAt: Long = dayOffset(0),
    ): StockMovement {
        return StockMovement(
            id = "0",
            productId = productId,
            productName = Name(productName),
            productUnit = Unit("pcs"),
            movementType = movementType,
            amount = Amount(BigDecimal(amount)),
            note = null,
            createdAt = createdAt,
        )
    }
}
