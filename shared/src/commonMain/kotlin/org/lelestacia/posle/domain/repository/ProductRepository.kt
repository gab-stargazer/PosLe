package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.data.entity.ProductWithVariantsAndStock
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant

interface ProductRepository {
    suspend fun addProduct(product: Product, imageByteArray: ByteArray?)
    fun readProducts(searchQuery: String): Flow<PagingData<Product>>
    fun readProductWithoutCategories(searchQuery: String): Flow<PagingData<Product>>
    fun readProductWithCategories(searchQuery: String, categoryId: Int): PagingSource<Int, ProductWithVariantsAndStock>
    fun readProductNotInCategory(searchQuery: String, categoryId: Int): PagingSource<Int, ProductWithVariantsAndStock>
    fun readAvailableProducts(searchQuery: String = ""): Flow<List<Product>>
    suspend fun updateProduct(
        product: Product,
        variantsToAdd: List<Variant>,
        variantsToRemove: List<Variant>,
        imageByteArray: ByteArray?
    )

    suspend fun deleteProduct(product: Product)
}
