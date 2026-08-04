package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.data.entity.StockEntity
import org.lelestacia.posle.data.entity.StockMovementEntity
import java.math.BigDecimal

/**
 * Data Access Object for Stock management.
 *
 * Tracks inventory levels by summing individual stock movement records
 * (Sales, Purchases, Returns, Adjustments).
 */
@Dao
interface StockDao {

    @Insert
    suspend fun insertNewStock(stock: StockEntity)

    @Insert
    suspend fun insertStockMovement(movement: StockMovementEntity)

    @Insert
    suspend fun insertStockMovements(movements: List<StockMovementEntity>)

    @Query(
        """
            SELECT * FROM stock_movement
            ORDER BY created_at DESC
        """
    )
    fun readStockMovement(): PagingSource<Int, StockMovementEntity>

    @Query(
        """
            SELECT * FROM stock_movement
            WHERE created_at >= :startDate AND created_at < :finishDate
            ORDER BY created_at DESC
        """
    )
    fun readStockMovementsInRange(
        startDate: Long,
        finishDate: Long
    ): Flow<List<StockMovementEntity>>

    @Query(
        """
            SELECT COALESCE(SUM(amount), 0.0) AS currentStock
            FROM stock_movement
            WHERE product_id = :productId
        """
    )
    suspend fun getStockByProductId(productId: String): BigDecimal

    @Update
    suspend fun updateStock(stock: StockEntity)
}