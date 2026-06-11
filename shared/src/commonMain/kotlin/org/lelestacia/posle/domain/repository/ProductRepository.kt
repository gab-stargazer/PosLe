package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.Product

interface ProductRepository {
    suspend fun addProduct(product: Product, imageByteArray: ByteArray?)
    fun readProduct(searchQuery: String): Flow<PagingData<Product>>
    suspend fun updateProduct(product: Product, imageByteArray: ByteArray?)
    suspend fun deleteProduct(product: Product)
}