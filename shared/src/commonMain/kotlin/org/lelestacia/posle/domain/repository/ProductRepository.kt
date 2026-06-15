package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant

interface ProductRepository {
    suspend fun addProduct(product: Product, imageByteArray: ByteArray?)
    suspend fun addVariant(variant: Variant)
    fun readProduct(searchQuery: String): Flow<PagingData<Product>>
    fun readVariant(): Flow<PagingData<Variant>>
    suspend fun updateProduct(product: Product, imageByteArray: ByteArray?)
    suspend fun deleteProduct(product: Product)
}