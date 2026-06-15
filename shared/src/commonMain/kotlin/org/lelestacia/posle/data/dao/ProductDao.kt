package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.ProductWithVariants

@Dao
interface ProductDao {

    @Insert
    suspend fun addProduct(product: ProductEntity): Long

    @Query("SELECT * FROM product WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun readProduct(name: String = ""): PagingSource<Int, ProductEntity>

    @Transaction
    @Query("SELECT * FROM product WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun readProductWithVariants(name: String = ""): PagingSource<Int, ProductWithVariants>

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun delete(id: Int)
}