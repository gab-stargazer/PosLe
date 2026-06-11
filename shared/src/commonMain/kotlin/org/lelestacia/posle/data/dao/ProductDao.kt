package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.lelestacia.posle.data.entity.ProductEntity

@Dao
interface ProductDao {

    @Insert
    suspend fun addProduct(product: ProductEntity)

    @Query("SELECT * FROM product WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun readProduct(name: String = ""): PagingSource<Int, ProductEntity>

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun delete(id: Int)
}