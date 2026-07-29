package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Price

interface StockRepository {

    fun readStockMovement(): Flow<PagingData<StockMovement>>
    suspend fun addStockMovement(
        productId: Int,
        amount: Amount,
        movementType: StockMovementType,
        buyPrice: Price? = null,
        note: String? = null
    )
}
