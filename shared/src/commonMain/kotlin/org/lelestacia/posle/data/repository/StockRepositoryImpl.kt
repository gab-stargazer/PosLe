package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.BatchDao
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.entity.BatchEntity
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.domain.repository.StockRepository
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Util.pagingConfig
import kotlin.time.Clock

class StockRepositoryImpl(
    private val dao: StockDao,
    private val batchDao: BatchDao,
    private val productDao: ProductDao,
): StockRepository {

    override fun readStockMovement(): Flow<PagingData<StockMovement>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                dao.readStockMovement()
            }
        ).flow.map {
            it.map(StockMovementEntity::toDomain)
        }
    }

    override suspend fun addStockMovement(
        productId: Int,
        amount: Amount,
        movementType: StockMovementType,
        buyPrice: Price?,
        note: String?
    ) {
        val product = productDao.readProductById(productId) ?: return
        val currentTime = Clock.System.now().toEpochMilliseconds()

        if (movementType == StockMovementType.Purchase || movementType == StockMovementType.AdjustmentIncrease || movementType == StockMovementType.Return) {
            val finalBuyPrice = buyPrice ?: productDao.getLatestBuyPriceByProductId(productId).price
            batchDao.insertBatch(
                BatchEntity(
                    productId = productId,
                    buyPrice = finalBuyPrice,
                    initialQuantity = amount,
                    currentQuantity = amount,
                    createdAt = currentTime
                )
            )
        }

        dao.insertStockMovement(
            StockMovementEntity(
                productId = productId,
                productName = product.name,
                productUnit = product.unit,
                movementType = movementType,
                amount = amount,
                note = note,
                createdAt = currentTime
            )
        )
    }
}
