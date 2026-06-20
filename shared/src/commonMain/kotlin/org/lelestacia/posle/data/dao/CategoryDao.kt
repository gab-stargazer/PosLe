package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.lelestacia.posle.data.entity.CategoryEntity
import org.lelestacia.posle.data.entity.ProductCategoryJunction

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    @Insert
    suspend fun insertConnection(connection: ProductCategoryJunction)

    @Query(
        """
            SELECT * FROM category   
            ORDER BY name ASC
        """
    )
    fun readCategories(): PagingSource<Int, CategoryEntity>

    @Query(
        """
            DELETE FROM category
            WHERE id = :categoryId
        """
    )
    suspend fun deleteCategoryById(categoryId: Int)

    @Query(
        """
            DELETE FROM product_category_junction 
            WHERE product_id = :productId AND category_id = :categoryId
        """
    )
    suspend fun deleteConnection(productId: Int, categoryId: Int)

    @Query(
        """
            DELETE FROM product_category_junction
            WHERE category_id = :categoryId
        """
    )
    suspend fun clearProductCategory(categoryId: Int)
}