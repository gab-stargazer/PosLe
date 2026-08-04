package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.domain.repository.StockRepository
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.UuidProvider
import org.lelestacia.posle.util.Util.pagingConfig
import kotlin.time.Clock

class StockRepositoryImpl(
    private val dao: StockDao,
    private val productDao: ProductDao,
): StockRepository {

    override fun getStockMovements(): Flow<PagingData<StockMovement>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                dao.readStockMovement()
            }
        ).flow.map {
            it.map(StockMovementEntity::toDomain)
        }
    }

    override fun getStockMovementsInRange(
        startDate: Long,
        finishDate: Long
    ): Flow<List<StockMovement>> {
        return dao.readStockMovementsInRange(startDate = startDate, finishDate = finishDate)
            .map { movements ->
                movements.map(StockMovementEntity::toDomain)
            }
    }

    override suspend fun createStockMovement(
        productId: String,
        amount: Amount,
        movementType: StockMovementType,
        note: String?
    ) {
        val product = productDao.readProductById(productId) ?: return
        
        dao.insertStockMovement(
            StockMovementEntity(
                id = UuidProvider.newUuid(),
                productId = productId,
                productName = product.name,
                productUnit = product.unit,
                movementType = movementType,
                amount = amount,
                note = note,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }
}
