package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.data.entity.VariantJunction

@Dao
interface VariantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: VariantEntity): Long

    @Query
        (
        """
            SELECT * FROM variant
            ORDER BY name ASC
        """
    )
    fun readVariant(): PagingSource<Int, VariantEntity>

    @Insert
    suspend fun insertVariantToProduct(variant: VariantJunction)

    @Query("DELETE FROM variant_junction WHERE product_id = :productId")
    suspend fun clearProductVariants(productId: Int)

    @Delete
    suspend fun deleteVariantToProduct(variant: VariantJunction)
}