package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.lelestacia.posle.data.entity.StockEntity
import org.lelestacia.posle.data.entity.StockMovementEntity
import java.math.BigDecimal

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
            SELECT COALESCE(SUM(amount), 0.0) AS currentStock
            FROM stock_movement
            WHERE product_id = :productId
        """
    )
    suspend fun getStockByProductId(productId: Int): BigDecimal

    @Update
    suspend fun updateStock(stock: StockEntity)
}