package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.Variant

interface VariantRepository {
    suspend fun addVariant(variant: Variant)
    fun readVariant(): Flow<PagingData<Variant>>
    fun readVariantByProductId(productId: Int): Flow<List<Variant>>
    suspend fun updateVariant(variant: Variant)
    suspend fun deleteVariant(variant: Variant)
}
