package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.util.Amount

/**
 * Repository interface for tracking stock movements (sales, purchases, adjustments).
 */
interface StockRepository {

    /**
     * Provides a paginated stream of all recorded stock movements.
     *
     * @return A [Flow] of [PagingData] containing [StockMovement] entries.
     */
    fun getStockMovements(): Flow<PagingData<StockMovement>>

    /**
     * Records a new stock movement for a product.
     *
     * @param productId The ID of the product affected.
     * @param amount The quantity moved (negative for sales/decreases).
     * @param movementType The reason/type of movement (Sale, Purchase, etc.).
     * @param note Optional descriptive note for the movement.
     */
    suspend fun createStockMovement(productId: Int, amount: Amount, movementType: StockMovementType, note: String? = null)
}
