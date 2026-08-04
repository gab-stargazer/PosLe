package org.lelestacia.posle.data.repository

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.lelestacia.posle.data.PosLeDB
import org.lelestacia.posle.data.TransactionRunnerImpl
import org.lelestacia.posle.data.createTestDatabase
import org.lelestacia.posle.data.entity.BundleEntity
import org.lelestacia.posle.data.entity.BundleProductEntity
import org.lelestacia.posle.data.entity.PriceChangeType
import org.lelestacia.posle.data.entity.ProductBuyPriceEntity
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.ProductSellPriceEntity
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleProductState
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.UuidProvider
import java.math.BigDecimal
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BundleRepositoryImplTest {

    private lateinit var db: PosLeDB
    private lateinit var repo: BundleRepositoryImpl
    private val storage = mockk<FileStorage>()

    private val timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()

    @AfterTest
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Test Cases
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `add product to bundle`() {
        runBlocking {
            // Given
            setup()
            val beras = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")
            val gula = insertProduct(name = "Gula Pasir 1kg", sku = "GP1-001", buyPrice = "15000", sellPrice = "18000")
            insertBundle(bundleId = "10", name = "Paket Sembako", products = listOf(beras))

            // When
            updateBundle(
                bundleId = "10",
                name = "Paket Sembako",
                products = listOf(
                    ProductInBundle(product = beras, quantity = "1", sellPrice = "90000"),
                    ProductInBundle(product = gula, quantity = "3", sellPrice = "17000")
                )
            )

            // Then
            val bundle = db.bundleDao().getBundleWithProductsById("10")!!
            assertEquals(2, bundle.products.size, "Bundle should have 2 products after adding Gula")
            assertEquals(1, bundle.products.count { it.product.id == beras.id }, "Bundle should still contain Beras")
            assertEquals(1, bundle.products.count { it.product.id == gula.id }, "Bundle should contain newly added Gula")
            assertEquals(
                BigDecimal("3"),
                bundle.products.find { it.product.id == gula.id }?.bundleProduct?.quantity?.value,
                "Gula quantity should be 3"
            )
        }
    }

    @Test
    fun `remove product from bundle`() {
        runBlocking {
            // Given
            setup()
            val beras = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")
            val gula = insertProduct(name = "Gula Pasir 1kg", sku = "GP1-001", buyPrice = "15000", sellPrice = "18000")
            insertBundle(bundleId = "10", name = "Paket Sembako", products = listOf(beras, gula))

            // When
            updateBundle(
                bundleId = "10",
                name = "Paket Sembako",
                products = listOf(
                    ProductInBundle(product = beras, quantity = "1", sellPrice = "90000")
                )
            )

            // Then
            val bundle = db.bundleDao().getBundleWithProductsById("10")!!
            assertEquals(1, bundle.products.size, "Bundle should have only 1 product after removing Gula")
            assertEquals(beras.id, bundle.products.first().product.id, "Remaining product should be Beras")
        }
    }

    @Test
    fun `update existing product quantity and price`() {
        runBlocking {
            // Given
            setup()
            val beras = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")
            insertBundle(bundleId = "10", name = "Paket Sembako", products = listOf(beras))

            // When
            updateBundle(
                bundleId = "10",
                name = "Paket Sembako",
                products = listOf(
                    ProductInBundle(product = beras, quantity = "2", sellPrice = "85000")
                )
            )

            // Then
            val bundle = db.bundleDao().getBundleWithProductsById("10")!!
            assertEquals(1, bundle.products.size, "Bundle should still have 1 product")
            assertEquals(
                BigDecimal("2"),
                bundle.products.first().bundleProduct.quantity.value,
                "Beras quantity should be updated to 2"
            )
            assertEquals(
                BigDecimal("85000"),
                bundle.products.first().bundleProduct.sellPrice.value,
                "Beras sell price should be updated to 85000"
            )
        }
    }

    @Test
    fun `mix of add update remove`() {
        runBlocking {
            // Given
            setup()
            val beras = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")
            val gula = insertProduct(name = "Gula Pasir 1kg", sku = "GP1-001", buyPrice = "15000", sellPrice = "18000")
            val minyak = insertProduct(name = "Minyak Goreng 2L", sku = "MG2-001", buyPrice = "28000", sellPrice = "33000")
            insertBundle(bundleId = "10", name = "Paket Sembako", products = listOf(beras, gula))

            // When
            updateBundle(
                bundleId = "10",
                name = "Paket Sembako",
                products = listOf(
                    ProductInBundle(product = gula, quantity = "1", sellPrice = "17500"),
                    ProductInBundle(product = minyak, quantity = "2", sellPrice = "32000")
                )
            )

            // Then
            val bundle = db.bundleDao().getBundleWithProductsById("10")!!
            assertEquals(2, bundle.products.size, "Bundle should have 2 products after mix operations")

            // Beras removed
            assertNull(
                bundle.products.find { it.product.id == beras.id },
                "Beras should be removed from bundle"
            )

            // Gula updated
            assertEquals(
                BigDecimal("1"),
                bundle.products.find { it.product.id == gula.id }?.bundleProduct?.quantity?.value,
                "Gula quantity should be updated to 1"
            )
            assertEquals(
                BigDecimal("17500"),
                bundle.products.find { it.product.id == gula.id }?.bundleProduct?.sellPrice?.value,
                "Gula sell price should be updated to 17500"
            )

            // Minyak added
            assertNotNull(
                bundle.products.find { it.product.id == minyak.id },
                "Minyak should be added to bundle"
            )
        }
    }

    @Test
    fun `returns early when bundle not found`() {
        runBlocking {
            // Given
            setup()
            insertProduct(name = "Test", sku = "TEST-001", buyPrice = "10000", sellPrice = "15000")

            // When & Then (no exception)
            updateBundle(
                bundleId = "999",
                name = "Tidak Ada",
                products = emptyList()
            )
            // Method should return early without throwing
        }
    }

    @Test
    fun `stores image via FileStorage when byteArray provided`() {
        runBlocking {
            // Given
            setup()
            val beras = insertProduct(name = "Beras 5kg", sku = "BR5-001", buyPrice = "80000", sellPrice = "90000")
            insertBundle(bundleId = "10", name = "Paket Sembako", products = listOf(beras))

            // When
            val imageData = byteArrayOf(0x01, 0x02, 0x03)
            updateBundle(
                bundleId = "10",
                name = "Paket Sembako",
                products = listOf(
                    ProductInBundle(product = beras, quantity = "1", sellPrice = "90000")
                ),
                imageByteArray = imageData
            )

            // Then
            val bundle = db.bundleDao().getBundleWithProductsById("10")!!
            assertEquals(
                "/mock/images/bundle_10.png",
                bundle.bundle.imageUri,
                "Bundle image URI should be updated to stored image path"
            )
            verify { storage.saveImage("bundle_10.png", imageData) }
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
        repo = BundleRepositoryImpl(
            bundleDao = db.bundleDao(),
            transactionRunner = TransactionRunnerImpl(db),
            storage = storage
        )
    }

    private data class ProductInBundle(
        val product: Product,
        val quantity: String,
        val sellPrice: String
    )

    private suspend fun insertProduct(
        name: String,
        sku: String,
        buyPrice: String,
        sellPrice: String
    ): Product {
        val id = UuidProvider.newUuid()
        val entity = ProductEntity(
            id = id,
            name = Name(name),
            unit = Unit("bungkus"),
            skuNumber = SkuNumber(sku),
            createdAt = timestamp
        )
        db.productDao().addProduct(entity)

        db.productDao().addBuyPrice(
            ProductBuyPriceEntity(
                id = UuidProvider.newUuid(),
                productId = id,
                price = Price(BigDecimal(buyPrice)),
                changeType = PriceChangeType.ProductCreation,
                createdAt = timestamp
            )
        )
        db.productDao().addSellPrice(
            ProductSellPriceEntity(
                id = UuidProvider.newUuid(),
                productId = id,
                price = Price(BigDecimal(sellPrice)),
                changeType = PriceChangeType.ProductCreation,
                createdAt = timestamp
            )
        )

        return Product(
            id = id,
            name = Name(name),
            stock = Amount(BigDecimal.ZERO),
            buyPrice = Price(BigDecimal.ZERO),
            sellPrice = Price(BigDecimal.ZERO),
            unit = Unit("bungkus"),
            skuNumber = SkuNumber(sku)
        )
    }

    private suspend fun insertBundle(
        bundleId: String,
        name: String,
        products: List<Product>
    ) {
        db.bundleDao().insertBundleAndGetId(
            BundleEntity(
                id = bundleId,
                name = Name(name),
                createdAt = timestamp
            )
        )

        val bundleProducts = products.map { product ->
            BundleProductEntity(
                id = UuidProvider.newUuid(),
                bundleId = bundleId,
                productId = product.id,
                name = product.name,
                quantity = Amount(BigDecimal.ONE),
                unit = product.unit,
                sellPrice = product.sellPrice,
                createdAt = timestamp,
                updatedAt = null
            )
        }
        db.bundleDao().insertBundleProducts(bundleProducts)
    }

    private suspend fun updateBundle(
        bundleId: String,
        name: String,
        products: List<ProductInBundle>,
        imageByteArray: ByteArray? = null
    ) {
        val bundleProducts = products.map { item ->
            BundleProductState(
                product = item.product,
                quantity = item.quantity,
                sellPrice = item.sellPrice
            )
        }

        repo.updateBundle(
            bundleId = bundleId,
            bundleName = Name(name),
            bundleProducts = bundleProducts,
            imageUri = null,
            imageByteArray = imageByteArray
        )
    }
}
