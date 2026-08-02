package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import org.lelestacia.posle.data.entity.BundleEntity
import org.lelestacia.posle.data.entity.BundleProductEntity
import org.lelestacia.posle.data.entity.BundleWithProductsEntity

@Dao
interface BundleDao {

    @Insert
    suspend fun insertBundleAndGetId(bundle: BundleEntity): Long

    @Update
    suspend fun updateBundle(bundle: BundleEntity)

    @Update
    suspend fun updateBundleProducts(bundleProducts: List<BundleProductEntity>)

    @Insert
    suspend fun insertBundleProducts(bundleProducts: List<BundleProductEntity>)

    @Query("DELETE FROM bundle_product WHERE bundle_id = :bundleId")
    suspend fun deleteBundleProductsByBundleId(bundleId: Int)

    @Query("DELETE FROM bundle_product WHERE bundle_id = :bundleId AND product_id IN (:productIds)")
    suspend fun deleteBundleProducts(bundleId: Int, productIds: List<Int>)

    @Query("DELETE FROM bundle WHERE id = :bundleId")
    suspend fun deleteBundleById(bundleId: Int)

    @Transaction
    @Query("SELECT * FROM bundle WHERE id = :id")
    suspend fun getBundleWithProductsById(id: Int): BundleWithProductsEntity?

    @Transaction
    @Query(
        """
            SELECT * FROM bundle
            WHERE name LIKE '%' || :name || '%'
            ORDER BY name ASC
        """
    )
    fun readAllBundlesByName(name: String): PagingSource<Int, BundleWithProductsEntity>
}
