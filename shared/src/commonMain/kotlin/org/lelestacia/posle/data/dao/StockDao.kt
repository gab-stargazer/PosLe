package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.lelestacia.posle.data.entity.StockEntity
import org.lelestacia.posle.data.entity.StockMovementEntity

@Dao
interface StockDao {

    @Insert
    suspend fun insertNewStock(stock: StockEntity)

    @Insert
    suspend fun insertStockMovement(movement: StockMovementEntity)

    @Query(
        """
            SELECT * FROM stock_movement
            ORDER BY created_at DESC
        """
    )
    fun readStockMovement(): PagingSource<Int, StockMovementEntity>

    @Query(
        """
            SELECT * FROM stock
            WHERE product_id = :productId
        """
    )
    suspend fun getStockByProductId(productId: Int): StockEntity

    @Update
    suspend fun updateStock(stock: StockEntity)
}