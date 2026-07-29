package org.lelestacia.posle.data.repository

import androidx.paging.PagingSource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.lelestacia.posle.data.PosLeDB
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.data.createTestDatabase
import org.lelestacia.posle.data.entity.PriceChangeType
import org.lelestacia.posle.data.entity.ProductBuyPriceEntity
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.ProductSellPriceEntity
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.BundleProduct
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit
import java.math.BigDecimal
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionRepositoryImplTest {

    private lateinit var db: PosLeDB
    private lateinit var repo: TransactionRepositoryImpl

    private val timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()

    @AfterTest
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Test Cases
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `insert product without stock tracking`() {
        runBlocking {
            // Given
            setup(isStockTracked = false)
            val productId = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")

            // When
            val cart = createProductCart(productId, quantity = "2")
            val transaction = repo.createTransaction(
                customerName = Name("Budi Santoso"),
                cartItems = cart
            )

            // Then
            assertEquals(
                "Budi Santoso",
                transaction.customerName.value,
                "Customer name should be preserved in transaction"
            )
            assertEquals(
                1,
                transaction.items.size,
                "Transaction should have 1 item"
            )
            assertEquals(
                TransactionItemType.Product,
                transaction.items[0].type,
                "Transaction item type should be Product"
            )
            assertEquals(
                "Beras 5kg",
                transaction.items[0].name.value,
                "Transaction item name should match product name"
            )
            assertEquals(
                BigDecimal("2"),
                transaction.items[0].quantity.value,
                "Transaction item quantity should match"
            )

            // And: No stock movements
            val movements = loadStockMovements()
            assertTrue(
                movements.isEmpty(),
                "No stock movements should be created when stock tracking is disabled"
            )
        }
    }

    @Test
    fun `insert product with stock tracking creates negated movement`() {
        runBlocking {
            // Given
            setup(isStockTracked = true)
            val productId = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")

            // When
            val cart = createProductCart(productId, quantity = "3")
            repo.createTransaction(
                customerName = Name("Siti Rahmawati"),
                cartItems = cart
            )

            // Then
            val movements = loadStockMovements()
            assertEquals(
                1,
                movements.size,
                "Should have 1 stock movement"
            )
            assertEquals(
                productId,
                movements[0].productId,
                "Stock movement should be for the correct product"
            )
            assertEquals(
                BigDecimal("-3"),
                movements[0].amount.value,
                "Stock movement amount should be negative for sales"
            )
            assertEquals(
                StockMovementType.Sale,
                movements[0].movementType,
                "Stock movement type should be Sale"
            )
        }
    }

    @Test
    fun `insert bundle with stock tracking multiplies quantities`() {
        runBlocking {
            // Given
            setup(isStockTracked = true)
            val berasId = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")
            val gulaId = insertProduct(name = "Gula Pasir 1kg", sku = "GP1-001", buyPrice = "15000", sellPrice = "18000")

            // When
            val bundle = createBundleCart(
                bundleId = 10,
                name = "Paket Sembako",
                products = listOf(
                    BundleItem(productId = berasId, name = "Beras 5kg", quantity = "1", unit = "bungkus", price = "90000"),
                    BundleItem(productId = gulaId, name = "Gula Pasir 1kg", quantity = "2", unit = "kg", price = "18000")
                ),
                bundleQuantity = "2",
                totalPrice = "310000"
            )
            repo.createTransaction(
                customerName = Name("Eko Prasetyo"),
                cartItems = bundle
            )

            // Then
            val movements = loadStockMovements()
            assertEquals(
                2,
                movements.size,
                "Should have 2 stock movements (one for each product)"
            )

            // Beras: 2 bundles × 1 each = -2
            val berasMovement = movements.first { it.productId == berasId }
            assertEquals(
                BigDecimal("-2"),
                berasMovement.amount.value,
                "Beras stock movement should be -2 (2 bundles × 1)"
            )

            // Gula: 2 bundles × 2 each = -4
            val gulaMovement = movements.first { it.productId == gulaId }
            assertEquals(
                BigDecimal("-4"),
                gulaMovement.amount.value,
                "Gula stock movement should be -4 (2 bundles × 2)"
            )
        }
    }

    @Test
    fun `customer name is preserved`() {
        runBlocking {
            // Given
            setup(isStockTracked = false)
            val productId = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")

            // When
            val cart = createProductCart(productId, quantity = "1")
            val transaction = repo.createTransaction(
                customerName = Name("Ibu Sumarni"),
                cartItems = cart
            )

            // Then
            assertEquals(
                "Ibu Sumarni",
                transaction.customerName.value,
                "Customer name should be preserved in transaction result"
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Helper Methods
    // ═══════════════════════════════════════════════════════════════════════

    private fun setup(isStockTracked: Boolean) {
        db = createTestDatabase()
        val settingManager = mockk<SettingManager>()
        every { settingManager.getSettings() } returns flowOf(
            PosLeSettings(isProductStockTracked = isStockTracked)
        )
        repo = TransactionRepositoryImpl(
            transactionDao = db.transactionDao(),
            productDao = db.productDao(),
            stockDao = db.stockDao(),
            settingManager = settingManager,
            transactionRunner = org.lelestacia.posle.data.TransactionRunnerImpl(db)
        )
    }

    private suspend fun insertProduct(
        name: String,
        sku: String,
        buyPrice: String,
        sellPrice: String
    ): Int {
        val entity = ProductEntity(
            id = 0,
            name = Name(name),
            unit = Unit("bungkus"),
            skuNumber = SkuNumber(sku),
            createdAt = timestamp
        )
        val id = db.productDao().addProduct(entity).toInt()

        db.productDao().addBuyPrice(
            ProductBuyPriceEntity(
                productId = id,
                price = Price(BigDecimal(buyPrice)),
                changeType = PriceChangeType.ProductCreation,
                createdAt = timestamp
            )
        )
        db.productDao().addSellPrice(
            ProductSellPriceEntity(
                productId = id,
                price = Price(BigDecimal(sellPrice)),
                changeType = PriceChangeType.ProductCreation,
                createdAt = timestamp
            )
        )
        return id
    }

    private fun createProductCart(productId: Int, quantity: String): List<CartItems.ProductCartItem> {
        return listOf(
            CartItems.ProductCartItem(
                id = 1,
                productId = productId,
                productName = Name("Beras 5kg"),
                skuNumber = SkuNumber("BR5-001"),
                imageUri = null,
                productQuantity = Amount(BigDecimal(quantity)),
                productBuyPrice = Price(BigDecimal("80000")),
                productSellPrice = Price(BigDecimal("90000")),
                productUnit = Unit("bungkus"),
                productNote = null
            )
        )
    }

    private data class BundleItem(
        val productId: Int,
        val name: String,
        val quantity: String,
        val unit: String,
        val price: String
    )

    private fun createBundleCart(
        bundleId: Int,
        name: String,
        products: List<BundleItem>,
        bundleQuantity: String,
        totalPrice: String
    ): List<CartItems.BundleCartItem> {
        val bundleProducts = products.map { item ->
            BundleProduct(
                productId = item.productId,
                productName = Name(item.name),
                skuNumber = SkuNumber(""),
                imageUri = null,
                buyPrice = Price(BigDecimal.ZERO),
                sellPrice = Price(BigDecimal(item.price)),
                sellPriceIndividual = Price(BigDecimal(item.price)),
                quantity = Amount(BigDecimal(item.quantity)),
                unit = Unit(item.unit),
                createdAt = timestamp,
                updatedAt = null
            )
        }
        return listOf(
            CartItems.BundleCartItem(
                id = 1,
                bundleId = bundleId,
                bundleName = Name(name),
                bundleQuantity = Amount(BigDecimal(bundleQuantity)),
                bundleTotalPrice = Price(BigDecimal(totalPrice)),
                bundleNote = null,
                bundleProducts = bundleProducts
            )
        )
    }

    private suspend fun loadStockMovements(): List<StockMovementEntity> {
        val result = db.stockDao().readStockMovement().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 100,
                placeholdersEnabled = false
            )
        )
        return when (result) {
            is PagingSource.LoadResult.Page -> result.data
            is PagingSource.LoadResult.Error -> throw result.throwable
            else -> throw AssertionError("Unexpected result: $result")
        }
    }
}
