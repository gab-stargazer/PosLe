package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.CategoryDao
import org.lelestacia.posle.data.entity.CategoryEntity
import org.lelestacia.posle.data.entity.ProductCategoryJunction
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.model.Category
import org.lelestacia.posle.domain.model.toEntity
import org.lelestacia.posle.domain.repository.CategoryRepository
import org.lelestacia.posle.util.Util.pagingConfig

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) : CategoryRepository {

    override suspend fun addCategory(category: Category) {
        dao.insertCategory(category.toEntity())
    }

    override suspend fun addProductToCategory(productId: Int, categoryId: Int) {
        val junction = ProductCategoryJunction(
            id = 0,
            productId = productId,
            categoryId = categoryId
        )

        dao.insertConnection(junction)
    }

    override suspend fun removeProductFromCategory(productId: Int, categoryId: Int) {
        dao.deleteConnection(productId, categoryId)
    }

    override fun readCategories(): Flow<PagingData<Category>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                dao.readCategories()
            }
        ).flow.map { it.map(CategoryEntity::toDomain) }
    }

    override suspend fun deleteCategory(categoryId: Int) {
        dao.clearProductCategory(categoryId)
        dao.deleteCategoryById(categoryId)
    }
}