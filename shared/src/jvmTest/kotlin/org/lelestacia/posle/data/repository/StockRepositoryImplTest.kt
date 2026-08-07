package org.lelestacia.posle.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.lelestacia.posle.data.PosLeDB
import org.lelestacia.posle.data.createTestDatabase
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Unit
import org.lelestacia.posle.util.UuidProvider
import java.math.BigDecimal
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StockRepositoryImplTest {

    private lateinit var db: PosLeDB
    private lateinit var repo: StockRepositoryImpl

    @AfterTest
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    @Test
    fun `getStockMovementsInRange returns only in-range movements`() {
        runBlocking {
            setup()
            val dayMs = 24 * 60 * 60 * 1000L
            val base = 1_700_000_000_000L

            // Three products + movements at distinct timestamps
            insertProduct(productId = "1")
            insertProduct(productId = "2")
            insertProduct(productId = "3")
            db.stockDao().insertStockMovement(movement(productId = "1", amount = "-5", createdAt = base))
            db.stockDao().insertStockMovement(movement(productId = "2", amount = "-3", createdAt = base + dayMs))
            db.stockDao().insertStockMovement(movement(productId = "3", amount = "-1", createdAt = base + 2 * dayMs))

            // Range covers only the first two (half-open: finishDate exclusive)
            val result = repo.getStockMovementsInRange(
                startDate = base,
                finishDate = base + 2 * dayMs
            ).first()

            assertEquals(2, result.size, "Only movements inside [start, finish) should be returned")
            assertEquals(listOf("2", "1"), result.map { it.productId }, "Newest first ordering")
        }
    }

    @Test
    fun `getStockMovementsInRange is exclusive of finish date`() {
        runBlocking {
            setup()
            val dayMs = 24 * 60 * 60 * 1000L
            val base = 1_700_000_000_000L

            insertProduct(productId = "1")
            insertProduct(productId = "2")
            db.stockDao().insertStockMovement(movement(productId = "1", amount = "-5", createdAt = base))
            db.stockDao().insertStockMovement(movement(productId = "2", amount = "-3", createdAt = base + dayMs))

            // finishDate equals the second movement's timestamp → it must be excluded
            val result = repo.getStockMovementsInRange(
                startDate = base,
                finishDate = base + dayMs
            ).first()

            assertEquals(1, result.size)
            assertEquals("1", result[0].productId)
        }
    }

    @Test
    fun `getStockMovementsInRange returns empty for empty range`() {
        runBlocking {
            setup()
            val result = repo.getStockMovementsInRange(startDate = 0L, finishDate = 1L).first()

            assertTrue(result.isEmpty())
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Helpers
    // ═══════════════════════════════════════════════════════════════════════

    private fun setup() {
        db = createTestDatabase()
        repo = StockRepositoryImpl(dao = db.stockDao(), productDao = db.productDao())
    }

    private suspend fun insertProduct(productId: String) {
        db.productDao().addProduct(
            ProductEntity(
                id = productId,
                name = Name("Produk $productId"),
                unit = Unit("pcs"),
                skuNumber = null,
                imageUri = null,
                createdAt = 1_700_000_000_000L,
                updatedAt = null,
            )
        )
    }

    private fun movement(productId: String, amount: String, createdAt: Long): StockMovementEntity {
        return StockMovementEntity(
            id = UuidProvider.newUuid(),
            productId = productId,
            productName = Name("Produk $productId"),
            productUnit = Unit("pcs"),
            movementType = StockMovementType.Sale,
            amount = Amount(BigDecimal(amount)),
            note = null,
            createdAt = createdAt,
        )
    }
}
