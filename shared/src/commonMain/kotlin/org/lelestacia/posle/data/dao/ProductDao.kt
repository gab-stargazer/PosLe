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

    @Transaction
    @Query(
        """
            SELECT product.* FROM product
            LEFT JOIN product_category_junction 
            ON product.id = product_category_junction.product_id
            WHERE product_category_junction.category_id IS NULL 
            AND name LIKE '%' || :searchQuery || '%'
            ORDER BY name ASC
        """
    )
    fun readProductWithoutCategories(searchQuery: String): PagingSource<Int, ProductWithVariants>

    @Transaction
    @Query(
        """
            SELECT product.* FROM product
            INNER JOIN product_category_junction 
            ON product.id = product_category_junction.product_id
            WHERE product_category_junction.category_id = :categoryId
            AND name LIKE '%' || :searchQuery || '%'
            ORDER BY name ASC
        """
    )
    fun readProductWithCategories(searchQuery: String, categoryId: Int): PagingSource<Int, ProductWithVariants>

    @Transaction
    @Query(
        """
            SELECT * FROM product 
            WHERE id NOT IN (
                SELECT product_id FROM product_category_junction WHERE category_id = :categoryId
            )
            AND name LIKE '%' || :searchQuery || '%'
            ORDER BY name ASC
        """
    )
    fun readProductNotInCategory(searchQuery: String, categoryId: Int): PagingSource<Int, ProductWithVariants>

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun delete(id: Int)
}