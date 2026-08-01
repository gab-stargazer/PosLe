package org.lelestacia.posle.data.repository

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.lelestacia.posle.data.PosLeDB
import org.lelestacia.posle.data.createTestDatabase
import org.lelestacia.posle.data.entity.PriceChangeType
import org.lelestacia.posle.data.entity.ProductBuyPriceEntity
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.ProductSellPriceEntity
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.data.entity.VariantJunction
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit
import java.math.BigDecimal
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProductRepositoryImplTest {

    private lateinit var db: PosLeDB
    private lateinit var repo: ProductRepositoryImpl
    private val storage = mockk<FileStorage>()

    private val timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()

    @AfterTest
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Test Cases: addProduct
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `addProduct creates entity with buy and sell prices`() {
        runBlocking {
            // Given
            setup()
            val product = createProduct(
                name = "Kopi Bubuk 200g",
                sku = "KB-001",
                buyPrice = "25000",
                sellPrice = "35000"
            )

            // When
            repo.createProduct(product, imageByteArray = null)

            // Then
            val created = db.productDao().getProductBySkuNumber("KB-001")
            assertNotNull(created, "Product should be created in database")

            assertEquals(
                "Kopi Bubuk 200g",
                created.product.name.value,
                "Product name should match"
            )

            val buyPrice = db.productDao().getLatestBuyPriceByProductId(created.product.id).price
            assertEquals(
                Price(BigDecimal("25000")),
                buyPrice,
                "Latest buy price should be 25000"
            )

            val sellPrice = db.productDao().getLatestSellPriceByProductId(created.product.id).price
            assertEquals(
                Price(BigDecimal("35000")),
                sellPrice,
                "Latest sell price should be 35000"
            )
        }
    }

    @Test
    fun `addProduct creates variant junctions`() {
        runBlocking {
            // Given
            setup()
            val sizeVariant = insertVariant(name = "Ukuran Besar", priceAdjustment = "5000")
            val originalVariant = insertVariant(name = "Original", priceAdjustment = "0")

            val product = createProduct(
                name = "Kopi Bubuk 200g",
                sku = "KB-001",
                buyPrice = "25000",
                sellPrice = "35000",
                variants = listOf(
                    Variant(sizeVariant.id, sizeVariant.name, sizeVariant.priceAdjustment),
                    Variant(originalVariant.id, originalVariant.name, originalVariant.priceAdjustment)
                )
            )

            // When
            repo.createProduct(product, imageByteArray = null)

            // Then
            val created = db.productDao().getProductBySkuNumber("KB-001")
            assertNotNull(created, "Product should be created in database")

            val productVariants = db.variantDao().readVariantByProductId(created.product.id).first()
            assertEquals(2, productVariants.size, "Product should have 2 variants linked")
        }
    }

    @Test
    fun `addProduct with image stores via FileStorage`() {
        runBlocking {
            // Given
            setup()
            val product = createProduct(
                name = "Kopi Bubuk 200g",
                sku = "KB-001",
                buyPrice = "25000",
                sellPrice = "35000"
            )

            // When
            val imageData = byteArrayOf(0x01, 0x02, 0x03)
            repo.createProduct(product, imageByteArray = imageData)

            // Then
            verify { storage.saveImage("Kopi Bubuk 200g.png", imageData) }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Test Cases: updateProduct
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `updateProduct records buy price change`() {
        runBlocking {
            // Given
            setup()
            val productId = insertProduct(name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")

            // When
            val updatedProduct = createProduct(id = productId, name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "27000", sellPrice = "35000")
            repo.updateProduct(
                product = updatedProduct,
                variantsToAdd = emptyList(),
                variantsToRemove = emptyList(),
                imageByteArray = null
            )

            // Then
            val priceHistory = db.productDao().readProductBuyPriceHistory(productId).first()
            assertEquals(2, priceHistory.size, "Price history should have 2 entries (creation + adjustment)")
            assertEquals(
                Price(BigDecimal("27000")),
                priceHistory[0].price,
                "Latest buy price should be updated to 27000"
            )
        }
    }

    @Test
    fun `updateProduct records sell price change`() {
        runBlocking {
            // Given
            setup()
            val productId = insertProduct(name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")

            // When
            val updatedProduct = createProduct(id = productId, name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "37000")
            repo.updateProduct(
                product = updatedProduct,
                variantsToAdd = emptyList(),
                variantsToRemove = emptyList(),
                imageByteArray = null
            )

            // Then
            val priceHistory = db.productDao().readProductSellPriceHistory(productId).first()
            assertEquals(2, priceHistory.size, "Price history should have 2 entries (creation + adjustment)")
            assertEquals(
                Price(BigDecimal("37000")),
                priceHistory[0].price,
                "Latest sell price should be updated to 37000"
            )
        }
    }

    @Test
    fun `updateProduct does not record price change when prices unchanged`() {
        runBlocking {
            // Given
            setup()
            val productId = insertProduct(name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")

            // When
            val updatedProduct = createProduct(id = productId, name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")
            repo.updateProduct(
                product = updatedProduct,
                variantsToAdd = emptyList(),
                variantsToRemove = emptyList(),
                imageByteArray = null
            )

            // Then
            assertEquals(
                1,
                db.productDao().readProductBuyPriceHistory(productId).first().size,
                "Buy price history should have only 1 entry (creation only)"
            )
            assertEquals(
                1,
                db.productDao().readProductSellPriceHistory(productId).first().size,
                "Sell price history should have only 1 entry (creation only)"
            )
        }
    }

    @Test
    fun `updateProduct adds variant junctions`() {
        runBlocking {
            // Given
            setup()
            val productId = insertProduct(name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")
            val variant = insertVariant(name = "Large", priceAdjustment = "5000")

            // When
            val updatedProduct = createProduct(id = productId, name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")
            repo.updateProduct(
                product = updatedProduct,
                variantsToAdd = listOf(Variant(variant.id, variant.name, variant.priceAdjustment)),
                variantsToRemove = emptyList(),
                imageByteArray = null
            )

            // Then
            val productVariants = db.variantDao().readVariantByProductId(productId).first()
            assertEquals(1, productVariants.size, "Product should have 1 variant after adding")
            assertEquals(variant.id, productVariants[0].id, "Added variant should match")
        }
    }

    @Test
    fun `updateProduct removes variant junctions`() {
        runBlocking {
            // Given
            setup()
            val productId = insertProduct(name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")
            val variant = insertVariant(name = "Large", priceAdjustment = "5000")
            db.variantDao().insertVariantToProduct(VariantJunction(productId = productId, variantId = variant.id))

            // When
            val updatedProduct = createProduct(id = productId, name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")
            repo.updateProduct(
                product = updatedProduct,
                variantsToAdd = emptyList(),
                variantsToRemove = listOf(Variant(variant.id, variant.name, variant.priceAdjustment)),
                imageByteArray = null
            )

            // Then
            val productVariants = db.variantDao().readVariantByProductId(productId).first()
            assertEquals(0, productVariants.size, "Product should have 0 variants after removal")
        }
    }

    @Test
    fun `updateProduct with image stores via FileStorage`() {
        runBlocking {
            // Given
            setup()
            val productId = insertProduct(name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")

            // When
            val updatedProduct = createProduct(id = productId, name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")
            val imageData = byteArrayOf(0xAB.toByte(), 0xCD.toByte())
            repo.updateProduct(
                product = updatedProduct,
                variantsToAdd = emptyList(),
                variantsToRemove = emptyList(),
                imageByteArray = imageData
            )

            // Then
            verify { storage.saveImage("Kopi Bubuk 200g.png", imageData) }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Test Cases: deleteProduct
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `deleteProduct removes from database`() {
        runBlocking {
            // Given
            setup()
            val productId = insertProduct(name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")
            val product = createProduct(id = productId, name = "Kopi Bubuk 200g", sku = "KB-001", buyPrice = "25000", sellPrice = "35000")

            // When
            repo.deleteProduct(product)

            // Then
            val queried = db.productDao().readProductById(productId)
            assertNull(queried, "Product should be null after deletion")
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Helper Methods
    // ═══════════════════════════════════════════════════════════════════════

    private fun setup() {
        db = createTestDatabase()
        clearMocks(storage)
        every { storage.saveImage(any(), any()) } answers { "/mock/images/${firstArg<String>()}" }
        every { storage.deleteImage(any()) } returns true
        repo = ProductRepositoryImpl(
            storage = storage,
            productDao = db.productDao(),
            variantDao = db.variantDao(),
            stockDao = db.stockDao(),
            categoryDao = db.categoryDao(),
            transactionRunner = org.lelestacia.posle.data.TransactionRunnerImpl(db)
        )
    }

    private fun createProduct(
        id: Int = 0,
        name: String,
        sku: String,
        buyPrice: String,
        sellPrice: String,
        variants: List<Variant> = emptyList()
    ): Product {
        return Product(
            id = id,
            name = Name(name),
            stock = Amount(BigDecimal.ZERO),
            buyPrice = Price(BigDecimal(buyPrice)),
            sellPrice = Price(BigDecimal(sellPrice)),
            unit = Unit("bungkus"),
            skuNumber = SkuNumber(sku),
            variants = variants
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

    private suspend fun insertVariant(
        name: String,
        priceAdjustment: String
    ): VariantEntity {
        val entity = VariantEntity(
            id = 0,
            name = Name(name),
            priceAdjustment = Price(BigDecimal(priceAdjustment))
        )
        val id = db.variantDao().insertVariant(entity).toInt()
        return entity.copy(id = id)
    }
}
