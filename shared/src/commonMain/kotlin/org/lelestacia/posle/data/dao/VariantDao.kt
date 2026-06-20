package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
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

    @Query(
        """
            SELECT variant.* FROM variant
            INNER JOIN variant_junction 
            ON variant.id = variant_junction.variant_id
            WHERE variant_junction.product_id = :productId
        """
    )
    fun readVariantByProductId(productId: Int): Flow<List<VariantEntity>>

    @Insert
    suspend fun insertVariantToProduct(variant: VariantJunction)

    @Query(
        """
            DELETE FROM variant_junction
            WHERE product_id == :productId AND variant_id == :variantId
        """
    )
    suspend fun deleteVariantToProduct(variantId: Int, productId: Int)

    @Query("DELETE FROM variant_junction WHERE product_id = :productId")
    suspend fun clearProductVariants(productId: Int)

    @Update
    suspend fun updateVariant(variant: VariantEntity)

    @Delete
    suspend fun deleteVariant(variant: VariantEntity)

    @Query(
        """
            DELETE FROM variant_junction
            WHERE variant_id == :variantId
        """
    )
    suspend fun deleteVariantJunction(variantId: Int)
}