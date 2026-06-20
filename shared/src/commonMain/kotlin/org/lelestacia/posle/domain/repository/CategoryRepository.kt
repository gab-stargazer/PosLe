package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.Category

interface CategoryRepository {

    suspend fun addCategory(category: Category)
    suspend fun addProductToCategory(productId: Int, categoryId: Int)
    suspend fun removeProductFromCategory(productId: Int, categoryId: Int)
    fun readCategories(): Flow<PagingData<Category>>
    suspend fun deleteCategory(categoryId: Int)
}