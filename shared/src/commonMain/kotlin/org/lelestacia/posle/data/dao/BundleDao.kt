package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.lelestacia.posle.data.entity.BundleEntity
import org.lelestacia.posle.data.entity.BundleProductEntity
import org.lelestacia.posle.data.entity.BundleWithProductsEntity

@Dao
interface BundleDao {

    @Insert
    suspend fun insertBundleAndGetId(bundle: BundleEntity): Long

    @Insert
    suspend fun insertBundleProducts(bundleProducts: List<BundleProductEntity>)

    @Query(
        """
            SELECT * FROM bundle
            WHERE name LIKE '%' || :name || '%'
            ORDER BY name ASC
        """
    )
    fun readAllBundlesByName(name: String): PagingSource<Int, BundleWithProductsEntity>
}