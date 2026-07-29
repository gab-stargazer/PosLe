package org.lelestacia.posle.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.lelestacia.posle.data.entity.BatchEntity
import java.math.BigDecimal

@Dao
interface BatchDao {

    @Insert
    suspend fun insertBatch(batch: BatchEntity): Long

    @Query(
        """
            SELECT * FROM batch 
            WHERE product_id = :productId AND current_quantity > 0.0
            ORDER BY created_at ASC
        """
    )
    suspend fun getActiveBatchesByProduct(productId: Int): List<BatchEntity>

    @Update
    suspend fun updateBatch(batch: BatchEntity)

    @Query("UPDATE batch SET current_quantity = :newQuantity WHERE id = :batchId")
    suspend fun updateBatchQuantity(batchId: Int, newQuantity: BigDecimal)
}
