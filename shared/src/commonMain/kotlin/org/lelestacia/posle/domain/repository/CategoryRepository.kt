package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.Category

/**
 * Repository interface for managing product categories and their relationships with products.
 */
interface CategoryRepository {

    /**
     * Persists a new category definition.
     *
     * @param category The [Category] model to create.
     */
    suspend fun createCategory(category: Category)

    /**
     * Associates a product with a category.
     *
     * @param productId The unique ID of the product.
     * @param categoryId The unique ID of the category.
     */
    suspend fun createProductCategoryLink(productId: String, categoryId: String)

    /**
     * Removes the association between a product and a category.
     *
     * @param productId The product ID.
     * @param categoryId The category ID.
     */
    suspend fun deleteProductCategoryLink(productId: String, categoryId: String)

    /**
     * Provides a paginated stream of all available categories.
     *
     * @return A [Flow] of [PagingData] containing [Category]s.
     */
    fun getCategories(): Flow<PagingData<Category>>

    /**
     * Retrieves all categories as a simple list.
     *
     * @return A [Flow] list of [Category]s.
     */
    fun getAllCategories(): Flow<List<Category>>

    /**
     * Permanently deletes a category and removes all its product associations.
     *
     * @param categoryId The ID of the category to delete.
     */
    suspend fun deleteCategory(categoryId: String)
}